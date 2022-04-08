package br.com.kbmg.wshammeron.event.producer;

import br.com.kbmg.wshammeron.event.AbstractEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractProducer<O, T extends AbstractEvent<O>> {

    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    public abstract void publishEvent(HttpServletRequest request, O eventObject);

    protected String getCurrentBaseUrl(HttpServletRequest request) {
        return (request == null) ? null : ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
    }
}
