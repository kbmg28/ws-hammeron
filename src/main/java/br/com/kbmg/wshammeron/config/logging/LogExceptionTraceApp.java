package br.com.kbmg.wshammeron.config.logging;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogExceptionTraceApp {
    private String className;
    private Object methodName;
    private Integer lineOfClass;

    public LogExceptionTraceApp(StackTraceElement stackTraceElement) {
        this.className = stackTraceElement.getClassName();
        this.methodName = stackTraceElement.getMethodName();
        this.lineOfClass = stackTraceElement.getLineNumber();
    }
}
