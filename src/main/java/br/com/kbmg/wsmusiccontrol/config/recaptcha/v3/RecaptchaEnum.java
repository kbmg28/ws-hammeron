package br.com.kbmg.wsmusiccontrol.config.recaptcha.v3;

import lombok.Getter;

public enum RecaptchaEnum {
    LOGIN_ACTION("login");

    @Getter
    private final String value;

    RecaptchaEnum(String value) {
        this.value = value;
    }
}
