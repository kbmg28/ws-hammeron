package br.com.kbmg.wshammeron.event.producer;

import br.com.kbmg.wshammeron.event.OnSpaceRequestEvent;
import br.com.kbmg.wshammeron.model.Space;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class SpaceRequestProducer extends AbstractProducer<Space, OnSpaceRequestEvent> {

    @Override
    public void publishEvent(HttpServletRequest request, Space space) {
        String currentBaseUrl = getCurrentBaseUrl(request);

        OnSpaceRequestEvent event = new OnSpaceRequestEvent(space, request.getLocale(), currentBaseUrl);

        super.eventPublisher.publishEvent(event);
    }
}
