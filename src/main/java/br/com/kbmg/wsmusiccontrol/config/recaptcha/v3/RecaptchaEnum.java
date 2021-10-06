package br.com.kbmg.wsmusiccontrol.config.recaptcha.v3;

import lombok.Getter;

public enum RecaptchaEnum {
    LOGIN_ACTION("login"),
    REGISTER_ACTION("register"),
    REGISTER_CONFIRMATION_ACTION("register-confirmation"),
    REGISTER_PASSWORD_ACTION("register-password");

    @Getter
    private final String value;

    RecaptchaEnum(String value) {
        this.value = value;
    }
}
