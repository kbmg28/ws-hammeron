package br.com.kbmg.wsmusiccontrol.event.producer;

import br.com.kbmg.wsmusiccontrol.event.OnPasswordRecoveryEvent;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class PasswordRecoveryProducer extends AbstractProducer<UserApp, OnPasswordRecoveryEvent> {

    @Override
    public void publishEvent(HttpServletRequest request, UserApp userApp) {
        String currentBaseUrl = getCurrentBaseUrl(request);

        OnPasswordRecoveryEvent event = new OnPasswordRecoveryEvent(userApp, request.getLocale(), currentBaseUrl);

        super.eventPublisher.publishEvent(event);
    }
}
