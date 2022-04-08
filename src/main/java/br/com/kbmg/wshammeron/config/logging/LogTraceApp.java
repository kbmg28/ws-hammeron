package br.com.kbmg.wshammeron.config.logging;

import br.com.kbmg.wshammeron.config.security.UserCredentialsSecurity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class LogTraceApp {
    private UserCredentialsSecurity credentials;
    private String className;
    private Object methodName;
    private Map<Integer, String> args;
    private MethodInvocationTypeEnum methodInvocationType;
    private Long executionTime;
}
