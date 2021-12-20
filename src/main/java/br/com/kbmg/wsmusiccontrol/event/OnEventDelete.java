package br.com.kbmg.wsmusiccontrol.event;

import br.com.kbmg.wsmusiccontrol.event.view.EventDeletedData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class OnEventDelete extends AbstractEvent<EventDeletedData> {

    public OnEventDelete(EventDeletedData eventDeletedData, Locale locale, String appUrl) {
        super(eventDeletedData, locale, appUrl);
    }
}