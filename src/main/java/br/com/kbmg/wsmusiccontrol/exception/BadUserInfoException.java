package br.com.kbmg.wsmusiccontrol.exception;

import lombok.Getter;

public class BadUserInfoException extends RuntimeException {

    @Getter
    private final String email;

    public BadUserInfoException(String email, String msg) {
        super(msg);
        this.email = email;
    }
}
