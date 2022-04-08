package br.com.kbmg.wshammeron.config.logging;

import br.com.kbmg.wshammeron.config.security.UserCredentialsSecurity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogMessage {
    private UserCredentialsSecurity credentials;
    private String message;
}
