package br.com.kbmg.wshammeron.unit.service;

import br.com.kbmg.wshammeron.dto.event.EventMainDataDto;
import br.com.kbmg.wshammeron.model.Event;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.service.impl.SmsServiceImpl;
import br.com.kbmg.wshammeron.unit.BaseUnitTests;
import builder.EventBuilder;
import builder.UserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.Set;
import java.util.function.BiConsumer;

import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.EVENT_SMS_DELETE;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.EVENT_SMS_NEW_OR_UPDATE;
import static constants.BaseTestsConstants.ANY_VALUE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SmsServiceTest extends BaseUnitTests {

    @InjectMocks
    private SmsServiceImpl smsService;

    @Mock
    private RestTemplate restTemplateMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        when(restTemplateMock.postForObject(anyString(), any(), any())).thenReturn(ANY_VALUE);
    }

    @Test
    void sendNotificationRememberEvent_shouldRequestApiSms() {
        UserApp userApp = UserBuilder.generateUserAppLogged();

        smsService.sendNotificationRememberEvent(new EventMainDataDto(), Set.of(userApp), ANY_VALUE);

        assertAll(
                () -> verify(restTemplateMock).postForObject(anyString(), any(), any())
        );
    }

    @Test
    void sendNewOrUpdateSmsEvent_shouldRequestApiSms() {
        sendSmsFromEvent(smsService::sendNewOrUpdateSmsEvent, EVENT_SMS_NEW_OR_UPDATE);
    }

    @Test
    void sendCancelSmsEvent_shouldRequestApiSms() {
        sendSmsFromEvent(smsService::sendCancelSmsEvent, EVENT_SMS_DELETE);
    }

    void sendSmsFromEvent(BiConsumer<EventMainDataDto,Set<UserApp>> method, String smsMessage) {
        UserApp userApp = super.givenUserAppFull();
        Space space = super.givenSpace(userApp);
        Event event = givenNextEvent(userApp, space);
        EventMainDataDto eventMainDataDto = EventBuilder.generateEventMainDataDto(event);

        when(messagesServiceMock.get(smsMessage)).thenReturn("%s");

        method.accept(eventMainDataDto, Set.of(userApp));

        assertAll(
                () -> verify(restTemplateMock).postForObject(anyString(), any(), any()),
                () -> verify(messagesServiceMock).get(smsMessage)
        );
    }
}
