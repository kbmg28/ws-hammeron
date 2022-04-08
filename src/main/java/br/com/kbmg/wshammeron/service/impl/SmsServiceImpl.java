package br.com.kbmg.wshammeron.service.impl;

import br.com.kbmg.wshammeron.config.logging.LogService;
import br.com.kbmg.wshammeron.config.messages.MessagesService;
import br.com.kbmg.wshammeron.dto.event.EventMainDataDto;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.service.SmsService;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SmsServiceImpl implements SmsService {
    private static final String URL_SMS = "https://api.mobizon.com.br/service/message/sendSmsMessage?apiKey=";
    private static final String RECIPIENT_KEY = "recipient";
    private static final String TEXT_KEY = "text";
    private static final String BR_NUMBER_TEMPLATE = "+55%s";

    @Value("${apiKeySms}")
    private String apiKeySms;

    @Autowired
    public MessagesService messagesService;

    @Autowired
    public LogService logService;

    @Override
    public void sendNewOrUpdateSmsEvent(EventMainDataDto event, Set<UserApp> userList) {
        Set<String> cellPhoneList = getCellPhoneList(userList);
        String nameDateTimeEvent = getNameAndDateAndTimeFormatted(event);
        String description = String.format(messagesService.get("event.sms.new.association"), nameDateTimeEvent);
        String messageText = String.format("[hammerOn]: %s", description);

        sendSms(cellPhoneList, messageText);
    }

    @Override
    public void sendCancelSmsEvent(EventMainDataDto event, Set<UserApp> userList) {
        Set<String> cellPhoneList = getCellPhoneList(userList);

        String nameDateTimeEvent = getNameAndDateAndTimeFormatted(event);
        String description = String.format(messagesService.get("event.sms.remove.association"), nameDateTimeEvent);
        String messageText = String.format("[hammerOn]: %s", description);

        sendSms(cellPhoneList, messageText);
    }

    @Override
    public void sendNotificationRememberEvent(EventMainDataDto eventInfo, Set<UserApp> userList, String description) {
        Set<String> cellPhoneList = getCellPhoneList(userList);

        String messageText = String.format("[hammerOn]: %s", description);

        sendSms(cellPhoneList, messageText);
    }

    private Set<String> getCellPhoneList(Set<UserApp> userList) {
        return userList.stream().map(UserApp::getCellPhone).collect(Collectors.toSet());
    }

    private String getNameAndDateAndTimeFormatted(EventMainDataDto event) {
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd/MM");
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");

        OffsetDateTime off = OffsetDateTime.of(event.getDateEvent(), event.getTimeEvent(), ZoneOffset.UTC);
        ZonedDateTime zoned = off.atZoneSameInstant(ZoneId.of(event.getTimeZoneName()));

        String formattedDate = zoned.format(formatterDate);
        String formattedTime = zoned.format(formatterTime);

        return String.format("%s (%s - %s)", event.getNameEvent(), formattedDate, formattedTime);
    }

    private void sendSms(Set<String> cellPhoneList, String messageText) {
        String url = URL_SMS + apiKeySms;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(new MediaType(MediaType.APPLICATION_FORM_URLENCODED, StandardCharsets.UTF_8));
        headers.setCacheControl(CacheControl.noCache());
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();

        map.put(TEXT_KEY, Collections.singletonList(messageText));

        cellPhoneList.forEach(cellPhone -> {
            String number = String.format(BR_NUMBER_TEMPLATE, cellPhone);
            map.put(RECIPIENT_KEY, Collections.singletonList(number));

            HttpEntity<MultiValueMap<String, String>> request =
                    new HttpEntity<>(map, headers);

            try {
                String res = restTemplate.postForObject(url, request, String.class);
                logService.logMessage(Level.INFO, String.format("Result sms send to %s: ->%s<-", number, res));
            } catch (Exception e) {
                logService.logMessage(Level.WARN, "Failed to send sms message to " + number);
            }
        });
    }

}
