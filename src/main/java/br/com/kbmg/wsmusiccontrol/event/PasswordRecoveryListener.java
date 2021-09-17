package br.com.kbmg.wsmusiccontrol.event;

import br.com.kbmg.wsmusiccontrol.config.messages.MessagesService;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.VerificationToken;
import br.com.kbmg.wsmusiccontrol.service.SecurityService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.UUID;

import static br.com.kbmg.wsmusiccontrol.constants.EmailConstants.CLIENT_EMAIL_VARIABLE_HTML;
import static br.com.kbmg.wsmusiccontrol.constants.EmailConstants.IS_YOUR_EMAIL_VARIABLE_HTML;
import static br.com.kbmg.wsmusiccontrol.constants.EmailConstants.MESSAGE_BEFORE_TOKEN_VARIABLE_HTML;
import static br.com.kbmg.wsmusiccontrol.constants.EmailConstants.PLEASE_CONFIRM_VARIABLE_HTML;
import static br.com.kbmg.wsmusiccontrol.constants.EmailConstants.TIME_QUANTITY_VARIABLE_HTML;
import static br.com.kbmg.wsmusiccontrol.constants.EmailConstants.TIME_TYPE_VARIABLE_HTML;
import static br.com.kbmg.wsmusiccontrol.constants.EmailConstants.TOKEN_VARIABLE_HTML;

@Component
@PropertySource("classpath:templates-html.properties")
public class PasswordRecoveryListener implements
        ApplicationListener<OnPasswordRecoveryEvent> {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    public MessagesService messagesService;

    @Value("${register.confirmation.token}")
    private String templateHtml;

    @Override
    public void onApplicationEvent(OnPasswordRecoveryEvent event) {
        this.recoveryPassword(event);
    }

    private void recoveryPassword(OnPasswordRecoveryEvent event) {
        UserApp userApp = event.getUserApp();

        String recipientAddress = userApp.getEmail();
        String subject = messagesService.get("user.email.password.recovery.subject");

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        String temporaryPassword = generateTemporaryPassword();

        String templateHtmlWithToken = getTemplateHtmlWithToken(temporaryPassword, recipientAddress);

        userAppService.encodePasswordAndSave(userApp, temporaryPassword);

        try {
            MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true);
            email.setTo(recipientAddress);
            email.setSubject(subject);

            email.setText(templateHtmlWithToken, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new ServiceException(messagesService.get("user.email.password.recovery.failed.send"));
        }
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 6);
    }

    private String getTemplateHtmlWithToken(String token, String recipientAddress) {
        templateHtml = templateHtml.replace(MESSAGE_BEFORE_TOKEN_VARIABLE_HTML, messagesService.get("user.email.password.recovery.title"));
        templateHtml = templateHtml.replace(TOKEN_VARIABLE_HTML, token);
        templateHtml = templateHtml.replace(PLEASE_CONFIRM_VARIABLE_HTML, messagesService.get("user.email.password.recovery.second.message.pt1"));
        templateHtml = templateHtml.replace(CLIENT_EMAIL_VARIABLE_HTML, recipientAddress);
        templateHtml = templateHtml.replace(IS_YOUR_EMAIL_VARIABLE_HTML, messagesService.get("user.email.password.recovery.second.message.pt2"));
        templateHtml = templateHtml.replace(TIME_QUANTITY_VARIABLE_HTML, String.valueOf(VerificationToken.EXPIRATION_TIME_MINUTES));
        templateHtml = templateHtml.replace(TIME_TYPE_VARIABLE_HTML, messagesService.get("time.type.minutes"));

        return templateHtml;
    }
}