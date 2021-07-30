package br.com.kbmg.wsmusiccontrol.exception;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException() {
        super();
    }

    public AuthorizationException(String msg) {
        super(msg);
    }
}
