package br.com.kbmg.wsmusiccontrol.repository;

import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.SpaceUserAppAssociation;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.repository.projection.OverviewProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SpaceUserAppAssociationRepository extends
        JpaRepository<SpaceUserAppAssociation, String> {

    SpaceUserAppAssociation findByUserAppAndLastAccessedSpaceTrue(UserApp userApp);

    @Query("SELECT su from SpaceUserAppAssociation su " +
            "join su.userApp u " +
            "where su.space = :space AND u.email IN :userEmailList")
    Set<SpaceUserAppAssociation> findBySpaceAndEmailUserList(Space space, Set<String> userEmailList);

    Optional<SpaceUserAppAssociation> findBySpaceAndUserApp(Space space, UserApp userApp);

    @Query(value = "SELECT up.PERMISSION AS \"groupName\", COUNT(suaa.ID) AS \"total\" " +
            " FROM space_user_app_association suaa " +
            " JOIN USER_PERMISSION up ON up.SPACE_USER_APP_ASSOCIATION_ID = suaa.id" +
            " WHERE suaa.SPACE_ID = :spaceId " +
            " GROUP BY up.PERMISSION", nativeQuery = true)
    List<OverviewProjection> findUserOverviewBySpace(String spaceId);
}
