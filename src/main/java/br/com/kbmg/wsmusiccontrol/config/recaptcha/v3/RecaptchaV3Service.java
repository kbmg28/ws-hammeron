package br.com.kbmg.wsmusiccontrol.config.recaptcha.v3;

public interface RecaptchaV3Service {
    default void processResponse(final String response) {}

    default void processResponse(final String response, String action) {}

    String getReCaptchaSite();

    String getReCaptchaSecret();
}
