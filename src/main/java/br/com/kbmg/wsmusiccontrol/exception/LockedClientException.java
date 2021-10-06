package br.com.kbmg.wsmusiccontrol.exception;

public class LockedClientException extends RuntimeException {
    public LockedClientException() {
        super();
    }

    public LockedClientException(String msg) {
        super(msg);
    }
}
