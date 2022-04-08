package br.com.kbmg.wshammeron.event;

import br.com.kbmg.wshammeron.event.view.SmsRememberData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class OnSmsTodayRememberEvent extends AbstractEvent<SmsRememberData> {

    public OnSmsTodayRememberEvent(SmsRememberData smsRememberData, Locale locale, String appUrl) {
        super(smsRememberData, locale, appUrl);
    }
}