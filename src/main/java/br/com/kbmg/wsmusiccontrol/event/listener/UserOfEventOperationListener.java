package br.com.kbmg.wsmusiccontrol.event.listener;

import br.com.kbmg.wsmusiccontrol.dto.event.EventMainDataDto;
import br.com.kbmg.wsmusiccontrol.enums.DatabaseOperationEnum;
import br.com.kbmg.wsmusiccontrol.event.OnUserOfEventOperation;
import br.com.kbmg.wsmusiccontrol.event.view.UserOfEventOperation;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.service.EventMailService;
import br.com.kbmg.wsmusiccontrol.service.SmsService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.stream.Collectors;

import static br.com.kbmg.wsmusiccontrol.constants.AppConstants.isRunningProdProfile;

@Component
@Slf4j
public class UserOfEventOperationListener
        extends AbstractEmailListener
        implements ApplicationListener<OnUserOfEventOperation> {

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private EventMailService eventMailService;

    @Autowired
    public SmsService smsService;

    @Value("${profile}")
    private String profile;

    @Override
    public void onApplicationEvent(OnUserOfEventOperation userOfEventOperation) {
        this.userOfEventOperationLogic(userOfEventOperation);
    }

    private void userOfEventOperationLogic(OnUserOfEventOperation springEvent) {
        UserOfEventOperation data = springEvent.getData();
        EventMainDataDto eventInfo = data.getEvent();
        Set<UserApp> userList = getUserListValidated(data);

        if(CollectionUtils.isEmpty(userList)) {
            String message = String.format("Not send email because user's email list is empty of eventHammerOn %s (%s)",
                    eventInfo.getNameEvent(),
                    eventInfo.getId());

            logService.logMessage(Level.INFO, message);
            return;
        }

        if (DatabaseOperationEnum.DELETE.equals(data.getOperation())) {
            eventMailService.sendCancelCalendarEvent(eventInfo, userList);
            smsService.sendCancelSmsEvent(eventInfo, userList);
        } else {
            eventMailService.sendNewOrUpdateCalendarEvent(eventInfo, userList);
            smsService.sendNewOrUpdateSmsEvent(eventInfo, userList);
        }
    }

    private Set<UserApp> getUserListValidated(UserOfEventOperation data) {
        Set<UserApp> userList = data.getUserList();

        if(isRunningProdProfile(profile)) {
            return userList;
        }

        return userList.stream().filter(UserApp::getIsSysAdmin).collect(Collectors.toSet());
    }

}