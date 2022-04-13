package builder;

import br.com.kbmg.wshammeron.enums.SpaceStatusEnum;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.SpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.model.UserPermission;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static constants.BaseTestsConstants.ANY_VALUE;

public abstract class SpaceBuilder {

    public static Space generateSpace(UserApp userApp) {
        Space space = new Space();

        space.setName(ANY_VALUE);
        space.setJustification(ANY_VALUE);
        space.setRequestedBy(userApp);
        space.setApprovedBy(userApp);
        space.setRequestedByDate(LocalDateTime.now());
        space.setApprovedByDate(LocalDateTime.now());
        space.setSpaceStatus(SpaceStatusEnum.APPROVED);

        return space;
    }

    public static SpaceUserAppAssociation generateSpaceUserAppAssociation(Space space, UserApp userApp, Set<UserPermission> userPermissions) {
        SpaceUserAppAssociation spaceUserAppAssociation = new SpaceUserAppAssociation();

        spaceUserAppAssociation.setSpace(space);
        spaceUserAppAssociation.setUserApp(userApp);
        spaceUserAppAssociation.setLastAccessedSpace(true);
        spaceUserAppAssociation.setActive(true);
        spaceUserAppAssociation.setUserPermissionList(userPermissions);

        space.getSpaceUserAppAssociationList().add(spaceUserAppAssociation);

        return spaceUserAppAssociation;
    }

}
