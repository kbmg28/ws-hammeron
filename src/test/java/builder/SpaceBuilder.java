package builder;

import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.SpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.UserApp;

import static constants.BaseTestsConstants.ANY_VALUE;

public abstract class SpaceBuilder {

    public static Space generateSpace(UserApp userApp) {
        Space space = new Space();

        space.setName(ANY_VALUE);
        space.setJustification(ANY_VALUE);
        space.setRequestedBy(userApp);
        space.setApprovedBy(userApp);


        return space;
    }

    public static SpaceUserAppAssociation generateSpaceUserAppAssociation(Space space, UserApp userApp, Boolean isOwner) {
        SpaceUserAppAssociation spaceUserAppAssociation = new SpaceUserAppAssociation();

        spaceUserAppAssociation.setSpace(space);
        spaceUserAppAssociation.setUserApp(userApp);
        space.getSpaceUserAppAssociationList().add(spaceUserAppAssociation);

        spaceUserAppAssociation.setLastAccessedSpace(true);

        return spaceUserAppAssociation;
    }

}
