package br.com.kbmg.wshammeron.service.impl;

import br.com.kbmg.wshammeron.config.logging.LogService;
import br.com.kbmg.wshammeron.config.recaptcha.v3.GoogleResponse;
import br.com.kbmg.wshammeron.config.recaptcha.v3.RecaptchaV3Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@Slf4j
public class RecaptchaVerifierServiceImpl {
    @Autowired
    private RecaptchaV3Config recaptchaV3Config;

    @Autowired
    private RestTemplate restClient;

    @Autowired
    private LogService logService;


    private static final String RECAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verify(String recaptchaResponse) {
        URI verifyUri = URI
                .create(String.format(RECAPTCHA_URL + "?secret=%s&response=%s", this.recaptchaV3Config.getSecret(), recaptchaResponse));

        boolean isVerified;
        try {
            GoogleResponse googleResponse = this.restClient.postForEntity(verifyUri, null, GoogleResponse.class).getBody();
            isVerified = googleResponse.isSuccess();
        } catch (Exception e) {
            logService.logUnexpectedException(e);
            isVerified = false;
        }
        return isVerified;
    }
}
