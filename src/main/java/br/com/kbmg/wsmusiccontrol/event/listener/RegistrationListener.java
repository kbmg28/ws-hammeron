package br.com.kbmg.wsmusiccontrol.event.listener;

import br.com.kbmg.wsmusiccontrol.config.messages.MessagesService;
import br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants;
import br.com.kbmg.wsmusiccontrol.event.OnRegistrationCompleteEvent;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.VerificationToken;
import br.com.kbmg.wsmusiccontrol.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static br.com.kbmg.wsmusiccontrol.constants.EmailConstants.CLIENT_EMAIL_VARIABLE_HTML;
import static br.com.kbmg.wsmusiccontrol.constants.EmailConstants.IS_YOUR_EMAIL_VARIABLE_HTML;
import static br.com.kbmg.wsmusiccontrol.constants.EmailConstants.MESSAGE_BEFORE_TOKEN_VARIABLE_HTML;
import static br.com.kbmg.wsmusiccontrol.constants.EmailConstants.PLEASE_CONFIRM_VARIABLE_HTML;
import static br.com.kbmg.wsmusiccontrol.constants.EmailConstants.TIME_QUANTITY_VARIABLE_HTML;
import static br.com.kbmg.wsmusiccontrol.constants.EmailConstants.TIME_TYPE_VARIABLE_HTML;
import static br.com.kbmg.wsmusiccontrol.constants.EmailConstants.TOKEN_VARIABLE_HTML;

@Component
@PropertySource("classpath:templates-html.properties")
public class RegistrationListener
        extends AbstractEmailListener
        implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    public MessagesService messagesService;

    @Value("${register.confirmation.token}")
    private String templateHtml;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        UserApp userApp = event.getData();
        String recipientAddress = userApp.getEmail();
        String subject = messagesService.get("user.email.verify.subject");
        String tokenStr = generateToken();
        String templateHtmlWithToken = getTemplateHtmlWithToken(tokenStr, recipientAddress);

        securityService.createVerificationToken(userApp, tokenStr.replaceAll("\\s+",""));

        super.sendEmail(recipientAddress, subject, templateHtmlWithToken, KeyMessageConstants.TOKEN_ACTIVATE_FAILED_SEND);
    }

    private String generateToken() {
        IntStream ints = new SecureRandom().ints(4, 1, 9);
        AtomicReference<String> token = new AtomicReference<>("");
        ints.forEach(num -> token.updateAndGet(v -> v + " " + num));

        return token.get();
    }

    private String getTemplateHtmlWithToken(String token, String recipientAddress) {
        String emailToClient = templateHtml.replace(MESSAGE_BEFORE_TOKEN_VARIABLE_HTML, messagesService.get("user.email.verify.title"));
        emailToClient = emailToClient.replace(TOKEN_VARIABLE_HTML, token);
        emailToClient = emailToClient.replace(PLEASE_CONFIRM_VARIABLE_HTML, messagesService.get("user.email.verify.confirm.second.message.pt1"));
        emailToClient = emailToClient.replace(CLIENT_EMAIL_VARIABLE_HTML, recipientAddress);
        emailToClient = emailToClient.replace(IS_YOUR_EMAIL_VARIABLE_HTML, messagesService.get("user.email.verify.confirm.second.message.pt2"));
        emailToClient = emailToClient.replace(TIME_QUANTITY_VARIABLE_HTML, String.valueOf(VerificationToken.EXPIRATION_TIME_MINUTES));
        emailToClient = emailToClient.replace(TIME_TYPE_VARIABLE_HTML, messagesService.get("time.type.minutes"));

        return emailToClient;
    }
}