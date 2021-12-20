package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.event.EventMainDataDto;
import br.com.kbmg.wsmusiccontrol.model.UserApp;

import java.util.Set;

public interface SmsService {

    void sendNewOrUpdateSmsEvent(EventMainDataDto event, Set<UserApp> userList);
    void sendCancelSmsEvent(EventMainDataDto event, Set<UserApp> userList);
    void sendNotificationRememberEvent(EventMainDataDto eventInfo, Set<UserApp> userList, String description);
}
