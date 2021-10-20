package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants;
import br.com.kbmg.wsmusiccontrol.dto.space.SpaceRequestDto;
import br.com.kbmg.wsmusiccontrol.event.producer.SpaceApproveProducer;
import br.com.kbmg.wsmusiccontrol.event.producer.SpaceRequestProducer;
import br.com.kbmg.wsmusiccontrol.exception.ForbiddenException;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.SpaceUserAppAssociation;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.repository.SpaceRepository;
import br.com.kbmg.wsmusiccontrol.service.SpaceService;
import br.com.kbmg.wsmusiccontrol.service.SpaceUserAppAssociationService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
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

    @Override
    public Space findOrCreatePublicSpace() {
        return this.repository.findByName(KeyMessageConstants.PUBLIC_SPACE).orElseGet(() -> {
            Space publicSpace = new Space();
            publicSpace.setName(KeyMessageConstants.PUBLIC_SPACE);
            publicSpace.setJustification("Default");

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
        space.setRequestedBy(userLogged);

        space.setRequestedByDate(LocalDateTime.now());
        repository.save(space);

        spaceRequestProducer.publishEvent(request, space);
    }

    @Override
    public Space findByIdAndUserAppValidated(Long spaceId, UserApp userApp) {
        if(userApp.isSysAdmin()) {
            return repository.findById(spaceId)
                    .orElseThrow(() -> new ServiceException(
                            String.format(messagesService.get("space.not.exist"), spaceId)
                    ));
        }

        return repository.findByIdAndUserApp(spaceId, userApp)
                .orElseThrow(() -> new ForbiddenException(
                        messagesService.get("space.user.not.access"))
                );
    }

    @Override
    public void approveNewSpaceForUser(Long idSpace, HttpServletRequest request) {
        repository.findById(idSpace).ifPresent(space -> {
            if (space.isApproved()) {
                return;
            }
            UserApp requestedBy = space.getRequestedBy();
            if(requestedBy == null) {
                throw new ServiceException(
                        messagesService.get("space.approve.notFound.requested")
                );
            }

            UserApp userLogged = userAppService.findUserLogged();

            space.setApprovedBy(userLogged);
            space.setApprovedByDate(LocalDateTime.now());
            repository.save(space);

            spaceUserAppAssociationService.createAssociationToParticipant(space, requestedBy);
            spaceUserAppAssociationService.createAssociationToSpaceOwner(space, requestedBy);

            spaceApproveProducer.publishEvent(request, space);
        });
    }

    @Override
    public List<Space> findAllSpaceToApprove() {
        return repository.findAllByApprovedByIsNull();
    }

    @Override
    public List<Space> findAllMySpaces() {
        UserApp userLogged = userAppService.findUserLogged();
        return userLogged.getSpaceUserAppAssociationList()
                .stream()
                .map(SpaceUserAppAssociation::getSpace)
                .collect(Collectors.toList());
    }

}
