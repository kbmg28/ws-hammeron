package br.com.kbmg.wshammeron.exception;

public class LockedClientException extends RuntimeException {
    public LockedClientException() {
        super();
    }

    public LockedClientException(String msg) {
        super(msg);
    }
}
