package br.com.kbmg.wsmusiccontrol.config.logging;

import br.com.kbmg.wsmusiccontrol.config.security.UserCredentialsSecurity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class LogObject {
    private UserCredentialsSecurity credentials;
    private String className;
    private Object methodName;
    private Map<Integer, String> args;
    private MethodInvocationTypeEnum methodInvocationType;
}
