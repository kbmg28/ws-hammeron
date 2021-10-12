package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.model.EventSpaceAssociation;
import br.com.kbmg.wsmusiccontrol.repository.EventSpaceAssociationRepository;
import br.com.kbmg.wsmusiccontrol.service.EventSpaceAssociationService;
import org.springframework.stereotype.Service;

@Service
public class EventSpaceAssociationServiceImpl
        extends GenericServiceImpl<EventSpaceAssociation, EventSpaceAssociationRepository>
        implements EventSpaceAssociationService {

}
