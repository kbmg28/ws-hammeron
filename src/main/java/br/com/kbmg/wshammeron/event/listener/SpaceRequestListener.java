package br.com.kbmg.wshammeron.event.listener;

import br.com.kbmg.wshammeron.event.OnSpaceRequestEvent;
import br.com.kbmg.wshammeron.exception.ServiceException;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.service.UserAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SpaceRequestListener
        extends AbstractEmailListener
        implements ApplicationListener<OnSpaceRequestEvent> {

    @Autowired
    private UserAppService userAppService;

    @Override
    public void onApplicationEvent(OnSpaceRequestEvent spaceRequestEvent) {
        this.spaceRequest(spaceRequestEvent);
    }

    private void spaceRequest(OnSpaceRequestEvent event) {
        Space space = event.getData();
        String subject = messagesService.get("space.request.subject").concat(space.getName());

        UserApp requestedBy = space.getRequestedBy();
        validateRequestedBy(requestedBy);

        Map<String, String> data = new HashMap<>();
        data.put("spaceName", space.getName());

        sendEmailToUserRequested(subject, data, requestedBy);
        sendEmailToSysAdmin(subject, data, space, requestedBy);
    }

    private void sendEmailToUserRequested(String subject, Map<String, String> data, UserApp requestedBy) {

        data.put("userName", requestedBy.getName());
        super.sendEmailFreeMarker(requestedBy.getEmail(),
                subject,
                "requestedSpace",
                data,
                "user.email.space.request.failed.send");
    }

    private void sendEmailToSysAdmin(String subject, Map<String, String> data, Space space, UserApp requestedBy) {
        List<UserApp> list = userAppService.findAllSysAdmin();
        Set<String> emailSysAdminList = list.stream().map(UserApp::getEmail).collect(Collectors.toSet());
        String[] emailsTo = emailSysAdminList.toArray(String[]::new);

        data.put("userName", "#SysAdmin");
        data.put("userRequestName", requestedBy.getName());
        data.put("userRequestEmail", requestedBy.getEmail());
        data.put("spaceJustification", space.getJustification());

        super.sendEmailFreeMarker(emailsTo,
                subject,
                "spaceRequestSysAdmin",
                data,
                "user.email.space.request.sysadmin.failed.send");
    }

    private void validateRequestedBy(UserApp requestedBy) {
        if (requestedBy == null) {
            throw new ServiceException(messagesService.get("user.email.space.request.failed.send"));
        }
    }

}