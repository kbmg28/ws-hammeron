package br.com.kbmg.wsmusiccontrol.config.logging;

import br.com.kbmg.wsmusiccontrol.config.security.SpringSecurityUtil;
import br.com.kbmg.wsmusiccontrol.config.security.UserCredentialsSecurity;
import br.com.kbmg.wsmusiccontrol.exception.AuthorizationException;
import br.com.kbmg.wsmusiccontrol.util.response.ResponseError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private ObjectMapper map;

    @Override
    public void logMessage(Level level, String message) {
        UserCredentialsSecurity credentials = SpringSecurityUtil.getCredentials();
        LogMessage logMessage = new LogMessage(credentials, message);

        executeLogByLevel(level, logMessage);
    }

    @Override
    public void logTraceApp(LogTraceApp logTraceApp) {
        executeLogByLevel(Level.INFO, logTraceApp);
    }

    @Override
    public void logUnexpectedException(Exception exception) {
        UserCredentialsSecurity credentials = SpringSecurityUtil.getCredentials();
        LogUnexpectedException logUnexpectedException =
                new LogUnexpectedException(credentials,
                        exception.getMessage(),
                        exception.getClass(),
                        Arrays.asList(exception.getStackTrace()));

        executeLogByLevel(Level.ERROR, logUnexpectedException);
    }

    @Override
    public void logExceptionWithStackTraceFilter(Exception ex, ResponseError responseError) {
        UserCredentialsSecurity credentials;
        List<LogExceptionTraceApp> logExceptionTraceAppList = Arrays.stream(ex.getStackTrace())
                .filter(trace -> trace.getClassName().startsWith("br.com.kbmg.wsmusiccontrol")
                        && trace.getLineNumber() > 0
                        && !trace.getClassName().contains(LoggingAdvice.class.getName()))
                .map(LogExceptionTraceApp::new)
                .collect(Collectors.toList());

        if (ex instanceof AuthorizationException) {
            AuthorizationException authorizationException = (AuthorizationException) ex;
            credentials = new UserCredentialsSecurity(authorizationException.getEmail());
        } else {
            credentials = SpringSecurityUtil.getCredentials();
        }

        LogErrorApp logErrorApp = new LogErrorApp(credentials, responseError, ex.getClass(), logExceptionTraceAppList);

        String json = parseJsonToString(logErrorApp);

        if(json != null) {
            log.error(json, ex);
        }
    }

    private <T> void executeLogByLevel(Level level, T logTraceApp) {
        String stringLogObject = parseJsonToString(logTraceApp);

        if (stringLogObject != null) {
            logByLevel(level, stringLogObject);
        }
    }

    private <T> String parseJsonToString(T obj) {
        try {
            map.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            this.logMessage(Level.WARN, "Failed parse json: " + obj.getClass());
        }
        return null;
    }

    private void logByLevel(Level logLevel, String message) {
        switch (logLevel) {
            case INFO:
                log.info(message);
                break;
            case WARN:
                log.warn(message);
                break;
            case ERROR:
                log.error(message);
                break;
            case DEBUG:
                log.debug(message);
                break;
            case TRACE:
                log.trace(message);
                break;
        }
    }

}
