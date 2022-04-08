package br.com.kbmg.wshammeron.service;

import br.com.kbmg.wshammeron.dto.space.overview.UserOverviewDto;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.SpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.UserApp;

import java.util.List;
import java.util.Set;

public interface SpaceUserAppAssociationService
        extends GenericService<SpaceUserAppAssociation>{

    void createAssociationWithPublicSpace(UserApp userApp);

    void createAssociationToParticipant(Space space, UserApp userApp);

    void createAssociationToSpaceOwner(Space space, UserApp userApp);

    SpaceUserAppAssociation findLastAccessedSpace(UserApp userApp);

    void updateLastAccessedSpace(UserApp userLogged, Space space);

    Set<SpaceUserAppAssociation> findAllBySpaceAndEmailList(Space space, Set<String> userEmailList);

    List<UserOverviewDto> findUserOverviewBySpace(Space space);
}
