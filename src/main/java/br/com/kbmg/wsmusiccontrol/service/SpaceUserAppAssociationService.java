package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.SpaceUserAppAssociation;
import br.com.kbmg.wsmusiccontrol.model.UserApp;

public interface SpaceUserAppAssociationService
        extends GenericService<SpaceUserAppAssociation>{

    void createAssociationWithPublicSpace(UserApp userApp);

    void createAssociationToParticipant(Space space, UserApp userApp);

    void createAssociationToSpaceOwner(Space space, UserApp userApp);
}
