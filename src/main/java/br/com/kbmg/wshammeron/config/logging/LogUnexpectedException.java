package br.com.kbmg.wshammeron.config.logging;

import br.com.kbmg.wshammeron.config.security.UserCredentialsSecurity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LogUnexpectedException {
    private UserCredentialsSecurity credentials;
    private String message;
    private Class<? extends Exception> exceptionClass;
    private List<StackTraceElement> stackTraceElements;
}
