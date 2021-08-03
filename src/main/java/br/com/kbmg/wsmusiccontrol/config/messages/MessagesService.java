package br.com.kbmg.wsmusiccontrol.config.messages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MessagesService {

    @Autowired
    @Qualifier("messageConfig")
    private MessageSource messageSource;

    public String get(String key) {
        LocaleContextHolder.getLocaleContext();
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocaleContext().getLocale());
    }

}