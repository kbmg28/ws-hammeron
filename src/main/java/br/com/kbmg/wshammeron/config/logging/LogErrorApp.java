package br.com.kbmg.wshammeron.config.logging;

import br.com.kbmg.wshammeron.config.security.UserCredentialsSecurity;
import br.com.kbmg.wshammeron.util.response.ResponseError;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LogErrorApp {
    private UserCredentialsSecurity credentials;
    private ResponseError errorDetails;
    private Class<? extends Exception> exceptionClass;
    private List<LogExceptionTraceApp> errorAppTrace;
}
