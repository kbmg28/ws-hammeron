package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.user.UserOnlyIdNameAndEmailDto;
import br.com.kbmg.wsmusiccontrol.model.Event;
import br.com.kbmg.wsmusiccontrol.model.EventSpaceUserAppAssociation;
import br.com.kbmg.wsmusiccontrol.model.Space;

import java.util.Set;

public interface EventSpaceUserAppAssociationService extends GenericService<EventSpaceUserAppAssociation>{
    Set<UserOnlyIdNameAndEmailDto> findAllUserAppByEvent(Event event);

    Set<EventSpaceUserAppAssociation> createAssociation(Space space, Event event, Set<String> userEmailList);
}
