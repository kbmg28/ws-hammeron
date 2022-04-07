package br.com.kbmg.wsmusiccontrol.repository;

import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.repository.projection.UserOnlyIdNameAndEmailProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAppRepository extends JpaRepository<UserApp, String> {

    Optional<UserApp> findByEmailIgnoreCase(String email);

    @Query("SELECT u from UserApp u where exists (" +
            "SELECT 1 from SpaceUserAppAssociation ass where ass.space = :space and ass.active = '1' and u = ass.userApp)")
    List<UserApp> findAllBySpace(Space space);

    @Query(value = "SELECT u.id AS \"userId\", u.name AS \"name\", u.email AS \"email\" " +
            "FROM USER_APP u WHERE u.ENABLED = '1' AND EXISTS ( " +
            "   SELECT 1 FROM SPACE_USER_APP_ASSOCIATION ass " +
            "       WHERE ass.space_id = :spaceId and ass.active = '1' and u.id = ass.user_app_id) " +
            "ORDER BY lower(u.name)", nativeQuery = true)
    List<UserOnlyIdNameAndEmailProjection> findUsersAssociationForEventsBySpace(String spaceId);

    List<UserApp> findByIsSysAdminTrue();
}
