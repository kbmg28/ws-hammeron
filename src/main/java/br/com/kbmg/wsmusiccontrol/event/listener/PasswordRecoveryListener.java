package br.com.kbmg.wsmusiccontrol.event.listener;

import br.com.kbmg.wsmusiccontrol.event.OnPasswordRecoveryEvent;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.VerificationToken;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

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
public class PasswordRecoveryListener extends AbstractEmailListener implements
        ApplicationListener<OnPasswordRecoveryEvent> {

    @Autowired
    private UserAppService userAppService;

    @Value("${register.confirmation.token}")
    private String templateHtml;

    @Override
    public void onApplicationEvent(OnPasswordRecoveryEvent event) {
        this.recoveryPassword(event);
    }

    private void recoveryPassword(OnPasswordRecoveryEvent event) {
        UserApp userApp = event.getData();

        String recipientAddress = userApp.getEmail();
        String subject = messagesService.get("user.email.password.recovery.subject");

        String temporaryPassword = generateTemporaryPassword();
        String templateHtmlWithToken = getTemplateHtmlWithToken(temporaryPassword, recipientAddress);

        userAppService.encodePasswordAndSave(userApp, temporaryPassword);
        super.sendEmail(recipientAddress, subject, templateHtmlWithToken, "user.email.password.recovery.failed.send");
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 6);
    }

    private String getTemplateHtmlWithToken(String token, String recipientAddress) {
        String emailToClient = templateHtml.replace(MESSAGE_BEFORE_TOKEN_VARIABLE_HTML, messagesService.get("user.email.password.recovery.title"));
        emailToClient = emailToClient.replace(TOKEN_VARIABLE_HTML, token);
        emailToClient = emailToClient.replace(PLEASE_CONFIRM_VARIABLE_HTML, messagesService.get("user.email.password.recovery.second.message.pt1"));
        emailToClient = emailToClient.replace(CLIENT_EMAIL_VARIABLE_HTML, recipientAddress);
        emailToClient = emailToClient.replace(IS_YOUR_EMAIL_VARIABLE_HTML, messagesService.get("user.email.password.recovery.second.message.pt2"));
        emailToClient = emailToClient.replace(TIME_QUANTITY_VARIABLE_HTML, String.valueOf(VerificationToken.EXPIRATION_TIME_MINUTES));
        emailToClient = emailToClient.replace(TIME_TYPE_VARIABLE_HTML, messagesService.get("time.type.minutes"));

        return emailToClient;
    }
}