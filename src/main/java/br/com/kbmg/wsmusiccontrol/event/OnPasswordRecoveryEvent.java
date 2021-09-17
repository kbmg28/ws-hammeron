package br.com.kbmg.wsmusiccontrol.event;

import br.com.kbmg.wsmusiccontrol.model.UserApp;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class OnPasswordRecoveryEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private UserApp userApp;

    public OnPasswordRecoveryEvent(
            UserApp userApp, Locale locale, String appUrl) {
        super(userApp);

        this.userApp = userApp;
        this.locale = locale;
        this.appUrl = appUrl;
    }

}