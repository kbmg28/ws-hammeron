package br.com.kbmg.wshammeron.event.listener;

import br.com.kbmg.wshammeron.enums.SpaceStatusEnum;
import br.com.kbmg.wshammeron.event.OnSpaceApproveEvent;
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

@Component
@Slf4j
public class SpaceApproveListener
        extends AbstractEmailListener
        implements ApplicationListener<OnSpaceApproveEvent> {

    @Autowired
    private UserAppService userAppService;

    @Override
    public void onApplicationEvent(OnSpaceApproveEvent spaceApproveEvent) {
        this.spaceRequest(spaceApproveEvent);
    }

    private void spaceRequest(OnSpaceApproveEvent event) {
        Space space = event.getData();
        String subject = messagesService.get("space.approve.subject").concat(" " + space.getName());

        UserApp requestedBy = space.getRequestedBy();
        UserApp approvedBy = space.getApprovedBy();
        validateRequestedByAndApprovedBy(requestedBy, approvedBy);

        String[] to = List.of(requestedBy.getEmail()).toArray(String[]::new);

        Map<String, String> data = new HashMap<>();
        data.put("userName", requestedBy.getName());
        data.put("spaceName", space.getName());

        sendEmailToUserRequested(space, subject, to, data);
    }

    private void sendEmailToUserRequested(Space space, String subject, String[] to, Map<String, String> data) {
        String templateName = SpaceStatusEnum.APPROVED.equals(space.getSpaceStatus()) ? "spaceRequestResultApproved" : "spaceRequestResultNegate";
        super.sendEmailFreeMarker(to, subject, templateName, data, "user.email.space.approve.failed.send");
    }

    private void validateRequestedByAndApprovedBy(UserApp requestedBy, UserApp approvedBy) {
        if (requestedBy == null || approvedBy == null) {
            throw new ServiceException("'requestedBy' and 'approvedBy' required");
        }
    }
}