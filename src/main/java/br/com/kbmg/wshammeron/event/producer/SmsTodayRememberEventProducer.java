package br.com.kbmg.wshammeron.event.producer;

import br.com.kbmg.wshammeron.event.OnSmsTodayRememberEvent;
import br.com.kbmg.wshammeron.event.view.SmsRememberData;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Service
public class SmsTodayRememberEventProducer extends AbstractProducer<SmsRememberData, OnSmsTodayRememberEvent> {

    @Override
    public void publishEvent(HttpServletRequest request, SmsRememberData smsRememberData) {
        String currentBaseUrl = getCurrentBaseUrl(request);

        OnSmsTodayRememberEvent springEvent = new OnSmsTodayRememberEvent(smsRememberData, Locale.getDefault(), currentBaseUrl);

        super.eventPublisher.publishEvent(springEvent);
    }
}
