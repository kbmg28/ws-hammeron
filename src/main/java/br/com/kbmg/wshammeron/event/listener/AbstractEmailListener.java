package br.com.kbmg.wshammeron.event.listener;

import br.com.kbmg.wshammeron.config.logging.LogService;
import br.com.kbmg.wshammeron.config.messages.MessagesService;
import br.com.kbmg.wshammeron.constants.AppConstants;
import br.com.kbmg.wshammeron.exception.ServiceException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.Map;

@Component
@Slf4j
public abstract class AbstractEmailListener {

    @Value("${profile}")
    private String profile;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    public MessagesService messagesService;

    @Autowired
    public LogService logService;

    @Autowired
    protected Configuration configurationFreemarker;

    protected void sendEmailFreeMarker(String to, String subject, String templateName, Map<String, String> data, String messageErrorKey) {
        sendEmailFreeMarker(new String[]{to}, subject, templateName, data, messageErrorKey);
    }

    protected void sendEmailFreeMarker(String[] to, String subject, String templateName, Map<String, String> data, String messageErrorKey) {
        try {
            if (profile == null) {
                logService.logMessage(Level.ERROR, "No profile");
            }
            String frontUrl = AppConstants.getFrontUrl(profile);

            data.put("currentYear", String.valueOf(LocalDate.now().getYear()));
            data.put("frontUrl", frontUrl);

            Template t = configurationFreemarker.getTemplate(templateName.concat(".ftl"));

            String htmlText = FreeMarkerTemplateUtils
                    .processTemplateIntoString(t, data);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true);
            email.setTo(to);
            email.setSubject(subject);

            email.setText(htmlText, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            logService.logUnexpectedException(e);
            throw new ServiceException(messagesService.get(messageErrorKey));
        }
    }

}