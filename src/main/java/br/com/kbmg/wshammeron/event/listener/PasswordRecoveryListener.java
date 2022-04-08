package br.com.kbmg.wshammeron.event.listener;

import br.com.kbmg.wshammeron.event.OnPasswordRecoveryEvent;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.service.UserAppService;
import br.com.kbmg.wshammeron.util.AppUtil;
import org.passay.CharacterRule;
import org.passay.PasswordGenerator;
import org.passay.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        LocalDateTime expireDate = LocalDateTime.now().plusMinutes(5);

        userAppService.encodePasswordAndSave(userApp, temporaryPassword, expireDate);

        Map<String, String> map = new HashMap<>();
        map.put("userName", userApp.getName());
        map.put("temporaryPassword", temporaryPassword);

        super.sendEmailFreeMarker(recipientAddress, subject, "temporaryPassword", map, "user.email.password.recovery.failed.send");
    }

    private String generateTemporaryPassword() {
        List<CharacterRule> rules = new ArrayList<>();
        for (Rule rule : AppUtil.getPasswordValidatorPattern().getRules()) {
            if (rule instanceof CharacterRule) {
                rules.add((CharacterRule) rule);
            }
        }

        PasswordGenerator passwordGenerator = new PasswordGenerator();
        return passwordGenerator.generatePassword(6, rules);
    }

}