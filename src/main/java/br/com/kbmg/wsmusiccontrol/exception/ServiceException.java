package br.com.kbmg.wsmusiccontrol.exception;

public class ServiceException extends RuntimeException {
    public ServiceException() {
        super();
    }

    public ServiceException(String msg) {
        super(msg);
    }
}
