package br.com.kbmg.wsmusiccontrol.event.listener;

import br.com.kbmg.wsmusiccontrol.config.messages.MessagesService;
import br.com.kbmg.wsmusiccontrol.event.OnSpaceRequestEvent;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SpaceRequestListener implements
        ApplicationListener<OnSpaceRequestEvent> {

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    public MessagesService messagesService;

    @Override
    public void onApplicationEvent(OnSpaceRequestEvent spaceRequestEvent) {
        this.spaceRequest(spaceRequestEvent);
    }

    private void spaceRequest(OnSpaceRequestEvent event) {
        Space space = event.getData();
        List<UserApp> list = userAppService.findAllSysAdmin();
        Set<String> emailSysAdminList = list.stream().map(UserApp::getEmail).collect(Collectors.toSet());

        String subject = messagesService.get("Requisição de Novo Espaço");

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true);
            email.setTo(emailSysAdminList.toArray(String[]::new));
            email.setSubject(subject);
            String html = "<html>\n" +
                    "<body>\n" +
                    "<h1>Solicitado por: " + space.getRequestedBy().getEmail() + "</h1>\n" +
                    "<br>\n" +
                    "<br>\n" +
                    "<p>Nome: " + space.getName() + " - Justificativa: " +  space.getJustification() + "</p>\n" +
                    "</body>\n" +
                    "</html>";

            email.setText(html, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new ServiceException(messagesService.get("user.email.password.recovery.failed.send"));
        }
    }
}