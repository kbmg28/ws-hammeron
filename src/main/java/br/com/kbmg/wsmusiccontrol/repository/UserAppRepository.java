package br.com.kbmg.wsmusiccontrol.repository;

import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAppRepository extends JpaRepository<UserApp, Long> {

    Optional<UserApp> findByEmail(String email);

    @Query("SELECT u from UserApp u where exists (" +
            "SELECT 1 from SpaceUserAppAssociation ass where ass.space = :space and u = ass.userApp)")
    List<UserApp> findAllBySpace(Space space);
}
