package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.event.EventMainDataDto;
import br.com.kbmg.wsmusiccontrol.model.UserApp;

import java.util.Set;

public interface EventMailService {
    
    void sendNewOrUpdateCalendarEvent(EventMainDataDto event, Set<UserApp> userList);
    void sendCancelCalendarEvent(EventMainDataDto event, Set<UserApp> userList);

    String sendNotificationRememberEvent(EventMainDataDto eventInfo, Set<UserApp> userList);
}
