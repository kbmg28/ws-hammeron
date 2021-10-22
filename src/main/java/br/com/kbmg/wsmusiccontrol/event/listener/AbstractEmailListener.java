package br.com.kbmg.wsmusiccontrol.event.listener;

import br.com.kbmg.wsmusiccontrol.config.logging.LogService;
import br.com.kbmg.wsmusiccontrol.config.messages.MessagesService;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Component
@Slf4j
public abstract class AbstractEmailListener {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    public MessagesService messagesService;

    @Autowired
    public LogService logService;

    protected void sendEmail(String to, String subject, String htmlText, String messageErrorKey) {
        sendEmail(new String[]{to}, subject, htmlText, messageErrorKey);
    }

    protected void sendEmail(String[] to, String subject, String htmlText, String messageErrorKey) {
        try {
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