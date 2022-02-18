package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.config.security.SpringSecurityUtil;
import br.com.kbmg.wsmusiccontrol.constants.AppConstants;
import br.com.kbmg.wsmusiccontrol.dto.space.SpaceRequestDto;
import br.com.kbmg.wsmusiccontrol.dto.space.overview.EventOverviewDto;
import br.com.kbmg.wsmusiccontrol.dto.space.overview.MusicOverviewDto;
import br.com.kbmg.wsmusiccontrol.dto.space.overview.SpaceOverviewDto;
import br.com.kbmg.wsmusiccontrol.dto.space.overview.UserOverviewDto;
import br.com.kbmg.wsmusiccontrol.enums.SpaceStatusEnum;
import br.com.kbmg.wsmusiccontrol.event.producer.SpaceApproveProducer;
import br.com.kbmg.wsmusiccontrol.event.producer.SpaceRequestProducer;
import br.com.kbmg.wsmusiccontrol.exception.ForbiddenException;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.SpaceUserAppAssociation;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.repository.SpaceRepository;
import br.com.kbmg.wsmusiccontrol.service.EventService;
import br.com.kbmg.wsmusiccontrol.service.JwtService;
import br.com.kbmg.wsmusiccontrol.service.MusicService;
import br.com.kbmg.wsmusiccontrol.service.SpaceService;
import br.com.kbmg.wsmusiccontrol.service.SpaceUserAppAssociationService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class SpaceServiceImpl
        extends GenericServiceImpl<Space, SpaceRepository>
        implements SpaceService {

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private SpaceUserAppAssociationService spaceUserAppAssociationService;

    @Autowired
    private SpaceRequestProducer spaceRequestProducer;

    @Autowired
    private SpaceApproveProducer spaceApproveProducer;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MusicService musicService;

    @Autowired
    private EventService eventService;

    @Override
    public Space findOrCreatePublicSpace() {
        return this.repository.findByName(AppConstants.DEFAULT_SPACE).orElseGet(() -> {
            Space publicSpace = new Space();
            publicSpace.setName(AppConstants.DEFAULT_SPACE);
            publicSpace.setJustification(AppConstants.DEFAULT_SPACE);
            publicSpace.setSpaceStatus(SpaceStatusEnum.APPROVED);

            return repository.save(publicSpace);
        });
    }

    @Override
    public void requestNewSpaceForUser(SpaceRequestDto spaceRequestDto, HttpServletRequest request) {
        this.repository.findByName(spaceRequestDto.getName())
                .ifPresent(space -> {
                    throw new ServiceException(String.format
                            (messagesService.get("space.already.exist"), space.getName()));
                });
        Space space = new Space();

        UserApp userLogged = userAppService.findUserLogged();

        space.setName(spaceRequestDto.getName());
        space.setJustification(spaceRequestDto.getJustification());
        space.setSpaceStatus(SpaceStatusEnum.REQUESTED);
        space.setRequestedBy(userLogged);

        space.setRequestedByDate(LocalDateTime.now());
        repository.save(space);

        spaceRequestProducer.publishEvent(request, space);
    }

    @Override
    public Space findByIdValidated(String spaceId) {
        return repository.findById(spaceId)
                .orElseThrow(() -> new ServiceException(
                        messagesService.get("space.not.exist")
                ));
    }

    @Override
    public Space findByIdAndUserAppValidated(String spaceId, UserApp userApp) {
        if(userApp.getIsSysAdmin()) {
            return findByIdValidated(spaceId);
        }

        return repository.findByIdAndSpaceStatusAndUserApp(spaceId, SpaceStatusEnum.APPROVED, userApp)
                .orElseThrow(() -> new ForbiddenException(
                        messagesService.get("space.user.not.access"))
                );
    }

    @Override
    public void approveNewSpaceForUser(String idSpace, SpaceStatusEnum spaceStatusEnum, HttpServletRequest request) {
        repository.findById(idSpace).ifPresent(space -> {
            if (space.isApproved()) {
                return;
            }
            UserApp requestedBy = space.getRequestedBy();

            validateParamsBeforeApprove(space, spaceStatusEnum, requestedBy);
            updateParamsToApprove(spaceStatusEnum, space);
            repository.save(space);

            spaceUserAppAssociationService.createAssociationToSpaceOwner(space, requestedBy);

            spaceApproveProducer.publishEvent(request, space);
        });
    }

    @Override
    public List<Space> findAllSpaceByStatus(SpaceStatusEnum spaceStatusEnum) {
        return repository.findAllBySpaceStatus(spaceStatusEnum);
    }

    @Override
    public List<Space> findAllSpacesByUserApp() {
        UserApp userLogged = userAppService.findUserLogged();

        if(userLogged.getIsSysAdmin()) {
            return this.findAll(Sort.by("name"));
        }

        return userLogged.getSpaceUserAppAssociationList()
                .stream()
                .map(SpaceUserAppAssociation::getSpace)
                .filter(space -> SpaceStatusEnum.APPROVED.equals(space.getSpaceStatus()))
                .sorted(Comparator.comparing(Space::getName))
                .collect(Collectors.toList());
    }

    @Override
    public String changeViewSpaceUser(String idSpace, HttpServletRequest request) {
        UserApp userLogged = userAppService.findUserLogged();
        Space space = findByIdAndUserAppValidated(idSpace, userLogged);

        if(!userLogged.getIsSysAdmin()) {
            spaceUserAppAssociationService.updateLastAccessedSpace(userLogged, space);
        }

        return jwtService.updateSpaceOnToken(request, space);
    }

    @Override
    public Space findLastAccessedSpace() {
        UserApp userLogged = userAppService.findUserLogged();

        if (userLogged.getIsSysAdmin()) {
            return this.findOrCreatePublicSpace();
        } else {
            SpaceUserAppAssociation ass = spaceUserAppAssociationService.findLastAccessedSpace(userLogged);

            return ass.getSpace();
        }
    }

    @Override
    public SpaceOverviewDto findSpaceOverview() {
        Space space = this.findByIdValidated(SpringSecurityUtil.getSpaceId());
        AtomicReference<String> createdBy = new AtomicReference<>(space.getCreatedByEmail());
        userAppService.findByEmail(createdBy.get()).ifPresent(user -> {
            String template = "%s (%s)";
            createdBy.set(String.format(template, user.getName(), user.getEmail()));
        });

        List<UserOverviewDto> userOverviewDtoList = spaceUserAppAssociationService.findUserOverviewBySpace(space);
        List<MusicOverviewDto> musicOverviewDtoList = musicService.findMusicOverview(space);
        List<EventOverviewDto> eventOverviewDtoList = eventService.findEventOverviewBySpace(space);
        return new SpaceOverviewDto(
                space.getId(),
                space.getName(),
                createdBy.get(),
                userOverviewDtoList,
                musicOverviewDtoList,
                eventOverviewDtoList
        );
    }

    private void updateParamsToApprove(SpaceStatusEnum spaceStatusEnum, Space space) {
        UserApp userLogged = userAppService.findUserLogged();

        space.setSpaceStatus(spaceStatusEnum);
        space.setApprovedBy(userLogged);
        space.setApprovedByDate(LocalDateTime.now());
    }

    private void validateParamsBeforeApprove(Space space, SpaceStatusEnum spaceStatusEnum,  UserApp requestedBy) {
        if(requestedBy == null) {
            throw new ServiceException(
                    messagesService.get("space.approve.notFound.requested")
            );
        }

        // TODO add status machine to space status
    }

}
