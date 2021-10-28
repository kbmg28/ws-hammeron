package br.com.kbmg.wsmusiccontrol.event.producer;

import br.com.kbmg.wsmusiccontrol.event.OnSpaceRequestEvent;
import br.com.kbmg.wsmusiccontrol.model.Space;
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
