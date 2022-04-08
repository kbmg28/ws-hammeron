package br.com.kbmg.wshammeron.config.logging;

import br.com.kbmg.wshammeron.util.response.ResponseError;
import org.slf4j.event.Level;

public interface LogService {

    void logMessage(Level level, String message);

    void logTraceApp(LogTraceApp logTraceApp);

    void logUnexpectedException(Exception exception);

    void logExceptionWithStackTraceFilter(Exception ex, ResponseError responseError);
}
