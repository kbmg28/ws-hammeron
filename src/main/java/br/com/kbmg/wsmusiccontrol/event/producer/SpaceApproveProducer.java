package br.com.kbmg.wsmusiccontrol.event.producer;

import br.com.kbmg.wsmusiccontrol.event.OnSpaceApproveEvent;
import br.com.kbmg.wsmusiccontrol.model.Space;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class SpaceApproveProducer extends AbstractProducer<Space, OnSpaceApproveEvent> {

    @Override
    public void publishEvent(HttpServletRequest request, Space space) {
        String currentBaseUrl = getCurrentBaseUrl(request);

        OnSpaceApproveEvent event = new OnSpaceApproveEvent(space, request.getLocale(), currentBaseUrl);

        super.eventPublisher.publishEvent(event);
    }
}
