package br.com.kbmg.wsmusiccontrol.event.listener;

import br.com.kbmg.wsmusiccontrol.config.logging.LogErrorApp;
import br.com.kbmg.wsmusiccontrol.config.messages.MessagesService;
import br.com.kbmg.wsmusiccontrol.config.security.SpringSecurityUtil;
import br.com.kbmg.wsmusiccontrol.config.security.UserCredentialsSecurity;
import br.com.kbmg.wsmusiccontrol.event.OnSpaceApproveEvent;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.Arrays;

@Component
@Slf4j
public class SpaceApproveListener implements
        ApplicationListener<OnSpaceApproveEvent> {

    private static Gson gson = new Gson();

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    public MessagesService messagesService;


    @Override
    public void onApplicationEvent(OnSpaceApproveEvent spaceApproveEvent) {
        this.spaceRequest(spaceApproveEvent);
    }

    private void spaceRequest(OnSpaceApproveEvent event) {
        Space space = event.getData();
        String subject = messagesService.get("space.approve.subject");

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true);

            UserApp requestedBy = space.getRequestedBy();
            UserApp approvedBy = space.getApprovedBy();

            validateRequestedByAndApprovedBy(requestedBy, approvedBy);

            email.setTo(Arrays.asList(requestedBy.getEmail(), approvedBy.getEmail()).toArray(String[]::new));
            email.setSubject(subject);

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

            email.setText(html, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            UserCredentialsSecurity credentials = SpringSecurityUtil.getCredentials();
            log.error(gson.toJson(new LogErrorApp(credentials, null, null)), e);
            throw new ServiceException(messagesService.get("user.email.space.approve.failed.send"));
        }
    }

    private void validateRequestedByAndApprovedBy(UserApp requestedBy, UserApp approvedBy) {
        if (requestedBy == null || approvedBy == null) {
            throw new ServiceException(messagesService.get("user.email.space.approve.failed.send"));
        }
    }
}