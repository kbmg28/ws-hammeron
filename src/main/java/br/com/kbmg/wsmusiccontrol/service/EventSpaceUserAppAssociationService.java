package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.user.UserDto;
import br.com.kbmg.wsmusiccontrol.model.Event;
import br.com.kbmg.wsmusiccontrol.model.EventSpaceUserAppAssociation;

import java.util.Set;

public interface EventSpaceUserAppAssociationService extends GenericService<EventSpaceUserAppAssociation>{
    Set<UserDto> findAllUserAppByEvent(Event event);
}
