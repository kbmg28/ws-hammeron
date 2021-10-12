package br.com.kbmg.wsmusiccontrol.repository;

import br.com.kbmg.wsmusiccontrol.model.SpaceUserAppAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpaceUserAppAssociationRepository extends
        JpaRepository<SpaceUserAppAssociation, Long> {

}
