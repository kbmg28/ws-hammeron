package br.com.kbmg.wsmusiccontrol.event.listener;

import br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants;
import br.com.kbmg.wsmusiccontrol.event.OnRegistrationCompleteEvent;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Component
@PropertySource("classpath:templates-html.properties")
public class RegistrationListener
        extends AbstractEmailListener
        implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private SecurityService securityService;

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

        securityService.createVerificationToken(userApp, tokenStr.replaceAll("\\s+",""));

        Map<String, String> map = new HashMap<>();
        map.put("userName", userApp.getName());
        map.put("token", tokenStr);

        super.sendEmailFreeMarker(recipientAddress,
                subject,
                "tokenActivateAccount",
                map,
                KeyMessageConstants.TOKEN_ACTIVATE_FAILED_SEND);

    }

    private String generateToken() {
        IntStream ints = new SecureRandom().ints(4, 1, 9);
        AtomicReference<String> token = new AtomicReference<>("");
        ints.forEach(num -> token.updateAndGet(v -> v + " " + num));

        return token.get();
    }

}