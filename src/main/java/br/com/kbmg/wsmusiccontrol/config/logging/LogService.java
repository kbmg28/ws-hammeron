package br.com.kbmg.wsmusiccontrol.config.logging;

import br.com.kbmg.wsmusiccontrol.util.response.ResponseError;
import org.slf4j.event.Level;

public interface LogService {

    void logMessage(Level level, String message);

    void logTraceApp(LogTraceApp logTraceApp);

    void logUnexpectedException(Exception exception);

    void logExceptionWithStackTraceFilter(Exception ex, ResponseError responseError);
}
