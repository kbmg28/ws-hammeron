package br.com.kbmg.wshammeron.config;

import br.com.kbmg.wshammeron.config.logging.LogService;
import br.com.kbmg.wshammeron.event.producer.SmsTodayRememberEventProducer;
import br.com.kbmg.wshammeron.event.view.SmsRememberData;
import br.com.kbmg.wshammeron.model.Event;
import br.com.kbmg.wshammeron.service.EventService;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableScheduling
public class SpringScheduler {

    @Autowired
    private EventService eventService;

    @Autowired
    private SmsTodayRememberEventProducer smsTodayRememberEventProducer;

    @Autowired
    private LogService logService;

    @Scheduled(cron = "${cron.expression.notification.event}")
    public void scheduleTaskUsingCronExpression() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<Event> events = eventService.findAllEventsByDateEvent(today);
        UUID uuidScheduler = UUID.randomUUID();

        logService.logMessage(Level.INFO, String.format("[SCHEDULER][%s]: Found %d events on %s",
                uuidScheduler,
                events.size(),
                today.format(formatterDate)));

        events.forEach(event -> smsTodayRememberEventProducer.publishEvent(null, new SmsRememberData(uuidScheduler.toString(), event)));

        logService.logMessage(Level.INFO, String.format("[SCHEDULER][%s]: successfully concluded",
                uuidScheduler));

    }
}
