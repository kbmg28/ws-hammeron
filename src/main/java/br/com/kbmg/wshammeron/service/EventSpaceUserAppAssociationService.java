package br.com.kbmg.wshammeron.service;

import br.com.kbmg.wshammeron.dto.event.EventMainDataDto;
import br.com.kbmg.wshammeron.dto.music.MusicSimpleToEventDto;
import br.com.kbmg.wshammeron.dto.user.UserOnlyIdNameAndEmailDto;
import br.com.kbmg.wshammeron.enums.DatabaseOperationEnum;
import br.com.kbmg.wshammeron.model.Event;
import br.com.kbmg.wshammeron.model.EventSpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.UserApp;

import java.util.List;
import java.util.Set;

public interface EventSpaceUserAppAssociationService extends GenericService<EventSpaceUserAppAssociation>{
    List<UserApp> findAllUserAppByEvent(Event event);

    Set<EventSpaceUserAppAssociation> createAssociation(Space space, Event event, Set<String> userEmailList, Set<MusicSimpleToEventDto> musicList);

    Set<EventSpaceUserAppAssociation> updateAssociations(Event eventInDatabase, Set<UserOnlyIdNameAndEmailDto> userList, Set<MusicSimpleToEventDto> musicList);

    void sendNotificationToAssociations(EventMainDataDto eventMainDataDto,
                                        Set<UserApp> userList,
                                        DatabaseOperationEnum operation);
}
