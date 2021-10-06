package br.com.kbmg.wsmusiccontrol.config.recaptcha.v3;

import br.com.kbmg.wsmusiccontrol.exception.LockedClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.net.URI;

@Service
@Slf4j
public class RecaptchaV3ServiceImpl extends AbstractCaptchaService{

    @Override
    public void processResponse(String response, final String action) {
        securityCheck(response);

        final URI verifyUri = URI.create(String.format(RECAPTCHA_URL_TEMPLATE, getReCaptchaSecret(), response, getClientIP()));
        try {
            final GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);
            log.debug("Google's response: {} ", googleResponse.toString());

            if (!googleResponse.isSuccess() || !googleResponse.getAction().equals(action) || googleResponse.getScore() < recaptchaV3Config.getThreshold()) {
                if (googleResponse.hasClientError()) {
                    recaptchaAttemptService.reCaptchaFailed(getClientIP());
                }
                throw new LockedClientException();
            }
        } catch (RestClientException rce) {
            throw new LockedClientException();
        }
        recaptchaAttemptService.reCaptchaSucceeded(getClientIP());
    }
}
