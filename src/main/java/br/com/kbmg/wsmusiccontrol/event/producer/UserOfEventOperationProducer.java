package br.com.kbmg.wsmusiccontrol.event.producer;

import br.com.kbmg.wsmusiccontrol.event.OnUserOfEventOperation;
import br.com.kbmg.wsmusiccontrol.event.view.UserOfEventOperation;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserOfEventOperationProducer extends AbstractProducer<UserOfEventOperation, OnUserOfEventOperation> {

    @Override
    public void publishEvent(HttpServletRequest request, UserOfEventOperation userOfEventOperation) {
        String currentBaseUrl = getCurrentBaseUrl(request);

        OnUserOfEventOperation springEvent = new OnUserOfEventOperation(userOfEventOperation, request.getLocale(), currentBaseUrl);

        super.eventPublisher.publishEvent(springEvent);
    }
}
