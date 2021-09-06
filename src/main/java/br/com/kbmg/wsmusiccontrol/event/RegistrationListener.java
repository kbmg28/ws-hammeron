package br.com.kbmg.wsmusiccontrol.event;

import br.com.kbmg.wsmusiccontrol.config.messages.MessagesService;
import br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.VerificationToken;
import br.com.kbmg.wsmusiccontrol.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static br.com.kbmg.wsmusiccontrol.constants.EmailConstants.*;

@Component
@PropertySource("classpath:templates-html.properties")
public class RegistrationListener implements
        ApplicationListener<OnRegistrationCompleteEvent> {

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
        UserApp userApp = event.getUserApp();

        String recipientAddress = userApp.getEmail();
        String subject = messagesService.get("user.email.verify.subject");

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        String tokenStr = generateToken();

        String templateHtmlWithToken = getTemplateHtmlWithToken(tokenStr, recipientAddress);

        securityService.createVerificationToken(userApp, tokenStr.replaceAll("\\s+",""));

        try {
            MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true);
            email.setTo(recipientAddress);
            email.setSubject(subject);

            email.setText(templateHtmlWithToken, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new ServiceException(messagesService.get(KeyMessageConstants.TOKEN_ACTIVATE_FAILED_SEND));
        }
    }

    private String generateToken() {
        IntStream ints = new SecureRandom().ints(4, 1, 9);
        AtomicReference<String> token = new AtomicReference<>("");
        ints.forEach(num -> token.updateAndGet(v -> v + " " + num));

        return token.get();
    }

    private String getTemplateHtmlWithToken(String token, String recipientAddress) {
        templateHtml = templateHtml.replace(MESSAGE_BEFORE_TOKEN_VARIABLE_HTML, messagesService.get("user.email.verify.title"));
        templateHtml = templateHtml.replace(TOKEN_VARIABLE_HTML, token);
        templateHtml = templateHtml.replace(PLEASE_CONFIRM_VARIABLE_HTML, messagesService.get("user.email.verify.confirm.second.message.pt1"));
        templateHtml = templateHtml.replace(CLIENT_EMAIL_VARIABLE_HTML, recipientAddress);
        templateHtml = templateHtml.replace(IS_YOUR_EMAIL_VARIABLE_HTML, messagesService.get("user.email.verify.confirm.second.message.pt2"));
        templateHtml = templateHtml.replace(TIME_QUANTITY_VARIABLE_HTML, String.valueOf(VerificationToken.EXPIRATION_TIME_MINUTES));
        templateHtml = templateHtml.replace(TIME_TYPE_VARIABLE_HTML, messagesService.get("time.type.minutes"));

        return templateHtml;
    }
}