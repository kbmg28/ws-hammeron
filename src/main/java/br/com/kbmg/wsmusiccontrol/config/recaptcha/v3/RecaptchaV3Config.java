package br.com.kbmg.wsmusiccontrol.config.recaptcha.v3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "google.recaptcha.key")
@Getter
@Setter
public class RecaptchaV3Config {
    private String site;
    private String secret;
    private float threshold;
}
