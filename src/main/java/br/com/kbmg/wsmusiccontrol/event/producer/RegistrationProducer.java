package br.com.kbmg.wsmusiccontrol.event.producer;

import br.com.kbmg.wsmusiccontrol.event.OnRegistrationCompleteEvent;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class RegistrationProducer extends AbstractProducer<UserApp, OnRegistrationCompleteEvent> {

    @Override
    public void publishEvent(HttpServletRequest request, UserApp userApp) {
        String currentBaseUrl = getCurrentBaseUrl(request);

        OnRegistrationCompleteEvent event = new OnRegistrationCompleteEvent(userApp, request.getLocale(), currentBaseUrl);

        super.eventPublisher.publishEvent(event);
    }
}
