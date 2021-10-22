package br.com.kbmg.wsmusiccontrol.event.listener;

import br.com.kbmg.wsmusiccontrol.event.OnSpaceApproveEvent;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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
        String subject = messagesService.get("space.approve.subject");

        UserApp requestedBy = space.getRequestedBy();
        UserApp approvedBy = space.getApprovedBy();
        validateRequestedByAndApprovedBy(requestedBy, approvedBy);

        String[] to = Arrays.asList(requestedBy.getEmail(), approvedBy.getEmail()).toArray(String[]::new);
        String html = "<html>\n" +
                "<body>\n" +
                "<h1>Solicitado por: " + requestedBy.getName() + " (" + requestedBy.getEmail() + ")" + "</h1>\n" +
                "<br>\n" +
                "<br>\n" +
                "<p>Nome: " + space.getName() + " - Justificativa: " +  space.getJustification() + "</p>\n" +
                "<br>\n" +
                "<br>\n" +
                "<p>Aprovado por: " + approvedBy.getName() + " (" + approvedBy.getEmail() + ")" + "</p>\n" +
                "</body>\n" +
                "</html>";

        super.sendEmail(to, subject, html, "user.email.space.approve.failed.send");
    }

    private void validateRequestedByAndApprovedBy(UserApp requestedBy, UserApp approvedBy) {
        if (requestedBy == null || approvedBy == null) {
            throw new ServiceException(messagesService.get("user.email.space.approve.failed.send"));
        }
    }
}