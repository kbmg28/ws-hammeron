package br.com.kbmg.wshammeron.config.messages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MessagesService {

    @Autowired
    @Qualifier("messageConfig")
    private MessageSource messageSource;

    public String get(String key) {
        LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
        Locale locale = (localeContext == null || localeContext.getLocale() == null) ?
                new Locale("pt", "BR") :
                localeContext.getLocale();
        return messageSource.getMessage(key, null, locale);
    }

}