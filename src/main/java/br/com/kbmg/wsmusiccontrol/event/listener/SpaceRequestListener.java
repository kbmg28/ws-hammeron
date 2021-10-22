package br.com.kbmg.wsmusiccontrol.event.listener;

import br.com.kbmg.wsmusiccontrol.config.messages.MessagesService;
import br.com.kbmg.wsmusiccontrol.event.OnSpaceRequestEvent;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SpaceRequestListener
        extends AbstractEmailListener
        implements ApplicationListener<OnSpaceRequestEvent> {

    private static Gson gson = new Gson();

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

        String subject = messagesService.get("space.request.subject");
        String[] emailsTo = emailSysAdminList.toArray(String[]::new);

        UserApp requestedBy = space.getRequestedBy();
        validateRequestedBy(requestedBy);

        String html = "<html>\n" +
                "<body>\n" +
                "<h1>Solicitado por: " + requestedBy.getEmail() + "</h1>\n" +
                "<br>\n" +
                "<br>\n" +
                "<p>Nome: " + space.getName() + " - Justificativa: " +  space.getJustification() + "</p>\n" +
                "</body>\n" +
                "</html>";

        super.sendEmail(emailsTo, subject, html, "user.email.space.request.failed.send");
    }

    private void validateRequestedBy(UserApp requestedBy) {
        if (requestedBy == null) {
            throw new ServiceException(messagesService.get("user.email.space.request.failed.send"));
        }
    }

}