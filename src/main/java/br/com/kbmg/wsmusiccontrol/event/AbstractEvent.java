package br.com.kbmg.wsmusiccontrol.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractEvent<T> extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private T data;

    public AbstractEvent(
            T data, Locale locale, String appUrl) {
        super(data);

        this.data = data;
        this.locale = locale;
        this.appUrl = appUrl;
    }

}