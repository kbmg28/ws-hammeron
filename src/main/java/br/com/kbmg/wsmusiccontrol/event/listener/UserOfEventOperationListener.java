package br.com.kbmg.wsmusiccontrol.event.listener;

import br.com.kbmg.wsmusiccontrol.dto.event.EventMainDataDto;
import br.com.kbmg.wsmusiccontrol.enums.DatabaseOperationEnum;
import br.com.kbmg.wsmusiccontrol.event.OnUserOfEventOperation;
import br.com.kbmg.wsmusiccontrol.event.view.UserOfEventOperation;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.service.EventMailService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;

@Component
@Slf4j
public class UserOfEventOperationListener
        extends AbstractEmailListener
        implements ApplicationListener<OnUserOfEventOperation> {

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private EventMailService eventMailService;

    @Override
    public void onApplicationEvent(OnUserOfEventOperation userOfEventOperation) {
        this.userOfEventOperationLogic(userOfEventOperation);
    }

    private void userOfEventOperationLogic(OnUserOfEventOperation springEvent) {
        UserOfEventOperation data = springEvent.getData();
        EventMainDataDto eventInfo = data.getEvent();
        Set<UserApp> userList = data.getUserList();

        if(CollectionUtils.isEmpty(userList)) {
            String message = String.format("Not send email because user's email list is empty of eventHammerOn %s (%s)",
                    eventInfo.getNameEvent(),
                    eventInfo.getId());

            logService.logMessage(Level.INFO, message);
            return;
        }

        if (DatabaseOperationEnum.DELETE.equals(data.getOperation())) {
            eventMailService.sendCancelCalendarEvent(eventInfo, userList);
        } else {
            eventMailService.sendNewOrUpdateCalendarEvent(eventInfo, userList);
        }
    }

    private void validateRequestedBy(UserApp requestedBy) {
        if (requestedBy == null) {
            throw new ServiceException(messagesService.get("user.email.space.request.failed.send"));
        }
    }

}