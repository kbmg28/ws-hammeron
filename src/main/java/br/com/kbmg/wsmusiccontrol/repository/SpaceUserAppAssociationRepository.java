package br.com.kbmg.wsmusiccontrol.repository;

import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.SpaceUserAppAssociation;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface SpaceUserAppAssociationRepository extends
        JpaRepository<SpaceUserAppAssociation, String> {

    Optional<SpaceUserAppAssociation> findBySpaceAndUserAppAndIsOwner(Space space, UserApp userApp, Boolean isOwner);

    SpaceUserAppAssociation findByUserAppAndLastAccessedSpaceTrue(UserApp userApp);

    @Query("SELECT su from SpaceUserAppAssociation su " +
            "join su.userApp u " +
            "where su.space = :space AND u.email IN :userEmailList")
    Set<SpaceUserAppAssociation> findBySpaceAndEmailUserList(Space space, Set<String> userEmailList);
}
