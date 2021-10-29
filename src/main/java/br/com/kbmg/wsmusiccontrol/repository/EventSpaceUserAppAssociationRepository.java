package br.com.kbmg.wsmusiccontrol.repository;

import br.com.kbmg.wsmusiccontrol.model.EventSpaceUserAppAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventSpaceUserAppAssociationRepository extends JpaRepository<EventSpaceUserAppAssociation, String> {

}
