package br.com.kbmg.wshammeron.unit.service;

import br.com.kbmg.wshammeron.config.recaptcha.v3.GoogleResponse;
import br.com.kbmg.wshammeron.config.recaptcha.v3.RecaptchaV3Config;
import br.com.kbmg.wshammeron.service.impl.RecaptchaVerifierServiceImpl;
import br.com.kbmg.wshammeron.unit.BaseUnitTests;
import builder.TokenBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static constants.BaseTestsConstants.SECRET_UNIT_TEST;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecaptchaVerifierServiceTest extends BaseUnitTests {

    @InjectMocks
    private RecaptchaVerifierServiceImpl recaptchaVerifierService;

    @Mock
    private RestTemplate restTemplateMock;

    @Mock
    private RecaptchaV3Config recaptchaV3ConfigMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void verify_shouldReturnTrue() {
        GoogleResponse googleResponse = TokenBuilder.generateGoogleResponse();

        when(recaptchaV3ConfigMock.getSecret()).thenReturn(SECRET_UNIT_TEST);
        when(restTemplateMock.postForEntity(any(URI.class), any(), any()))
                .thenReturn(new ResponseEntity<>(googleResponse, HttpStatus.OK));

        boolean result = recaptchaVerifierService.verify(SECRET_UNIT_TEST);

        assertAll(
                () -> verify(restTemplateMock).postForEntity(any(URI.class), any(), any()),
                () -> assertTrue(result)
        );
    }

    @Test
    void verify_shouldReturnFalse() {
        when(recaptchaV3ConfigMock.getSecret()).thenReturn(SECRET_UNIT_TEST);

        boolean result = recaptchaVerifierService.verify(SECRET_UNIT_TEST);

        assertAll(
                () -> verify(restTemplateMock).postForEntity(any(URI.class), any(), any()),
                () -> assertFalse(result)
        );
    }

}
