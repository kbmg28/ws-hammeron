package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.constants.AppConstants;
import br.com.kbmg.wsmusiccontrol.dto.space.overview.UserOverviewDto;
import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.SpaceUserAppAssociation;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.repository.SpaceUserAppAssociationRepository;
import br.com.kbmg.wsmusiccontrol.repository.projection.OverviewProjection;
import br.com.kbmg.wsmusiccontrol.service.SpaceService;
import br.com.kbmg.wsmusiccontrol.service.SpaceUserAppAssociationService;
import br.com.kbmg.wsmusiccontrol.service.UserPermissionService;
import br.com.kbmg.wsmusiccontrol.util.mapper.OverviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SpaceUserAppAssociationServiceImpl
        extends GenericServiceImpl<SpaceUserAppAssociation, SpaceUserAppAssociationRepository>
        implements SpaceUserAppAssociationService {

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private UserPermissionService userPermissionService;

    @Autowired
    private OverviewMapper overviewMapper;

    @Override
    public void createAssociationWithPublicSpace(UserApp userApp) {
        Space space = spaceService.findOrCreatePublicSpace();

        createAssociation(space, userApp, false);
    }

    @Override
    public void createAssociationToParticipant(Space space, UserApp userApp) {
        createAssociation(space, userApp, false);
    }

    @Override
    public void createAssociationToSpaceOwner(Space space, UserApp userApp) {
        createAssociation(space, userApp, true);
    }

    @Override
    public void updateLastAccessedSpace(UserApp userLogged, Space space) {

        SpaceUserAppAssociation associationOld = this.findLastAccessedSpace(userLogged);

        SpaceUserAppAssociation associationLast = userLogged
                .getSpaceUserAppAssociationList()
                .stream()
                .filter(ass -> ass.getSpace().getId().equals(space.getId()))
                .findFirst()
                .orElseThrow();

        if(associationOld != null) {
            associationOld.setLastAccessedSpace(false);
            repository.save(associationOld);
        }

        associationLast.setLastAccessedSpace(true);
        repository.save(associationLast);
    }

    @Override
    public Set<SpaceUserAppAssociation> findAllBySpaceAndEmailList(Space space, Set<String> userEmailList) {
        Set<SpaceUserAppAssociation> associationList = repository.findBySpaceAndEmailUserList(space, userEmailList);

        if(associationList.size() != userEmailList.size()) {
            throw new ServiceException(messagesService.get("event.user.list.invalid"));
        }

        return associationList;
    }

    @Override
    public List<UserOverviewDto> findUserOverviewBySpace(Space space) {
        List<OverviewProjection> list = repository.findUserOverviewBySpace(space.getId());
        List<UserOverviewDto> userOverviewDtoList = overviewMapper.toUserOverviewDtoList(list);

        Map<String, List<UserOverviewDto>> userOverviewMap = userOverviewDtoList.stream().collect(Collectors.groupingBy(UserOverviewDto::getPermissionName));
        Arrays.asList(PermissionEnum.values()).forEach(type -> {
            String typePermission = type.name();
            if(!userOverviewMap.containsKey(typePermission)) {
                userOverviewDtoList.add(new UserOverviewDto(typePermission, 0L));
            }
        });

        return userOverviewDtoList;
    }

    @Override
    public SpaceUserAppAssociation findLastAccessedSpace(UserApp userApp) {
        return repository.findByUserAppAndLastAccessedSpaceTrue(userApp);
    }

    private SpaceUserAppAssociation createAssociation(Space space, UserApp userApp, Boolean isSpaceOwner) {
        PermissionEnum newPermission = isSpaceOwner ? PermissionEnum.SPACE_OWNER : PermissionEnum.PARTICIPANT;

        SpaceUserAppAssociation association = repository.findBySpaceAndUserApp(space, userApp).orElseGet(() -> {
            SpaceUserAppAssociation newAssociation = new SpaceUserAppAssociation();
            newAssociation.setUserApp(userApp);
            newAssociation.setSpace(space);

            Boolean isDefaultSpace =  AppConstants.DEFAULT_SPACE.equals(space.getName());

            newAssociation.setLastAccessedSpace(isDefaultSpace);
            return newAssociation;
        });
        validateIfAssociationWithPermissionAlreadyExists(association, newPermission);
        association.setActive(true);
        repository.save(association);
        userPermissionService.addPermissionToUser(association, newPermission);

        return association;
    }

    private void validateIfAssociationWithPermissionAlreadyExists(SpaceUserAppAssociation association, PermissionEnum newPermission) {
        if (association.getId() == null) {
            return;
        }
        association.getUserPermissionList()
                .stream()
                .filter(up -> newPermission.equals(up.getPermission()))
                .findFirst()
                .ifPresent(userPermission -> {
                    throw new ServiceException(
                            messagesService.get("space.user.email.permission.already.exists")
                    );
                });
    }
}
