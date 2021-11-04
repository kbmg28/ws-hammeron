package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.SpaceUserAppAssociation;
import br.com.kbmg.wsmusiccontrol.model.UserApp;

import java.util.Set;

public interface SpaceUserAppAssociationService
        extends GenericService<SpaceUserAppAssociation>{

    void createAssociationWithPublicSpace(UserApp userApp);

    void createAssociationToParticipant(Space space, UserApp userApp);

    void createAssociationToSpaceOwner(Space space, UserApp userApp);

    SpaceUserAppAssociation findLastAccessedSpace(UserApp userApp);

    void updateLastAccessedSpace(UserApp userLogged, Space space);

    Set<SpaceUserAppAssociation> findAllBySpaceAndEmailList(Space space, Set<String> userEmailList);
}
