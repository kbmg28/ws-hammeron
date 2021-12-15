package br.com.kbmg.wsmusiccontrol.event;

import br.com.kbmg.wsmusiccontrol.event.view.UserOfEventOperation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class OnUserOfEventOperation extends AbstractEvent<UserOfEventOperation> {

    public OnUserOfEventOperation(UserOfEventOperation userOfEventAction, Locale locale, String appUrl) {
        super(userOfEventAction, locale, appUrl);
    }
}