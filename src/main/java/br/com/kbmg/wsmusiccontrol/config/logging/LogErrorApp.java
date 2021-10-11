package br.com.kbmg.wsmusiccontrol.config.logging;

import br.com.kbmg.wsmusiccontrol.config.security.UserCredentialsSecurity;
import br.com.kbmg.wsmusiccontrol.util.response.ResponseError;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LogErrorApp {
    private UserCredentialsSecurity credentials;
    private ResponseError errorDetails;
    private List<LogExceptionTraceApp> errorAppTrace;
}
