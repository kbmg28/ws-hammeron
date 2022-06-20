package br.com.kbmg.wshammeron.unit.service;

import br.com.kbmg.wshammeron.dto.event.EventMainDataDto;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.service.impl.SmsServiceImpl;
import br.com.kbmg.wshammeron.unit.BaseUnitTests;
import builder.UserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

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
    }

    @Test
    void sendNotificationRememberEvent_shouldRequestApiSms() {
        UserApp userApp = UserBuilder.generateUserAppLogged();

        when(restTemplateMock.postForObject(anyString(), any(), any())).thenReturn(ANY_VALUE);

        smsService.sendNotificationRememberEvent(new EventMainDataDto(), Set.of(userApp), ANY_VALUE);

        assertAll(
                () -> verify(restTemplateMock).postForObject(anyString(), any(), any())
        );
    }

}
