package br.com.kbmg.wshammeron.exception;

import lombok.Getter;

public class AuthorizationException extends RuntimeException {

    @Getter
    private String email;

    public AuthorizationException() {
        super();
    }

    public AuthorizationException(String email, String msg) {
        super(msg);
        this.email = email;
    }
}
