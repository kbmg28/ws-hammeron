package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.constants.AppConstants;
import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.SpaceUserAppAssociation;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.repository.SpaceUserAppAssociationRepository;
import br.com.kbmg.wsmusiccontrol.service.SpaceService;
import br.com.kbmg.wsmusiccontrol.service.SpaceUserAppAssociationService;
import br.com.kbmg.wsmusiccontrol.service.UserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SpaceUserAppAssociationServiceImpl
        extends GenericServiceImpl<SpaceUserAppAssociation, SpaceUserAppAssociationRepository>
        implements SpaceUserAppAssociationService {

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private UserPermissionService userPermissionService;

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

        associationOld.setLastAccessedSpace(false);
        associationLast.setLastAccessedSpace(true);

        repository.save(associationOld);
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

            return repository.save(newAssociation);
        });

        userPermissionService.addPermissionToUser(association, newPermission);

        return association;
    }
}
