package br.com.kbmg.wshammeron.service;

import br.com.kbmg.wshammeron.dto.event.EventMainDataDto;
import br.com.kbmg.wshammeron.model.UserApp;

import java.util.Set;

public interface SmsService {

    void sendNewOrUpdateSmsEvent(EventMainDataDto event, Set<UserApp> userList);
    void sendCancelSmsEvent(EventMainDataDto event, Set<UserApp> userList);
    void sendNotificationRememberEvent(EventMainDataDto eventInfo, Set<UserApp> userList, String description);
}
