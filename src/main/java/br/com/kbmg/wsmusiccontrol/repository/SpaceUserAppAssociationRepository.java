package br.com.kbmg.wsmusiccontrol.repository;

import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.SpaceUserAppAssociation;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpaceUserAppAssociationRepository extends
        JpaRepository<SpaceUserAppAssociation, String> {

    Optional<SpaceUserAppAssociation> findBySpaceAndUserAppAndIsOwner(Space space, UserApp userApp, Boolean isOwner);
}
