package br.com.kbmg.wshammeron.event;

import br.com.kbmg.wshammeron.model.UserApp;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class OnRegistrationCompleteEvent extends AbstractEvent<UserApp> {

    public OnRegistrationCompleteEvent(UserApp userApp, Locale locale, String appUrl) {
        super(userApp, locale, appUrl);
    }
}