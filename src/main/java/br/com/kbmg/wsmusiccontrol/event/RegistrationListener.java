package br.com.kbmg.wsmusiccontrol.event;

import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.UUID;

@Component
public class RegistrationListener implements
        ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        UserApp userApp = event.getUserApp();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(userApp, token);

        String recipientAddress = userApp.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl
                = event.getAppUrl() + "/regitrationConfirm.html?token=" + token;

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true);
            email.setTo(recipientAddress);
            email.setSubject(subject);
            String html = "<html>\n" +
                    "<body>\n" +
                    "<h1>%s</>\n" +
                    "</body>\n" +
                    "</html>";
            email.setText(String.format(html, "appUrl:" + event.getAppUrl() + " - Token:" + token), true);
            mailSender.send(mimeMessage)
            ;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}