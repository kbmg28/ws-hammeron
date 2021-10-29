package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants;
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
    public SpaceUserAppAssociation findLastAccessedSpace(UserApp userApp) {
        return repository.findByUserAppAndLastAccessedSpaceTrue(userApp);
    }

    private SpaceUserAppAssociation createAssociation(Space space, UserApp userApp, Boolean isOwner) {
        repository.findBySpaceAndUserAppAndIsOwner(space, userApp, isOwner).ifPresent(ass -> {
            throw new ServiceException(
                    messagesService.get("space.user.already.exists")
            );
        });

        userPermissionService.addPermissionToUser(userApp, isOwner ? PermissionEnum.SPACE_OWNER : PermissionEnum.PARTICIPANT);
        SpaceUserAppAssociation spaceUserAppAssociation = new SpaceUserAppAssociation();
        spaceUserAppAssociation.setUserApp(userApp);
        spaceUserAppAssociation.setSpace(space);
        spaceUserAppAssociation.setIsOwner(isOwner);

        spaceUserAppAssociation.setLastAccessedSpace(KeyMessageConstants.PUBLIC_SPACE.equals(space.getName()));

        return repository.save(spaceUserAppAssociation);
    }
}
