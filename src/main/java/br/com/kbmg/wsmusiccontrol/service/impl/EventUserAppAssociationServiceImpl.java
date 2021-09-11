package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.model.EventUserAppAssociation;
import br.com.kbmg.wsmusiccontrol.repository.EventUserAppAssociationRepository;
import br.com.kbmg.wsmusiccontrol.service.EventUserAppAssociationService;
import org.springframework.stereotype.Service;

@Service
public class EventUserAppAssociationServiceImpl
        extends GenericServiceImpl<EventUserAppAssociation, EventUserAppAssociationRepository>
        implements EventUserAppAssociationService {

}
