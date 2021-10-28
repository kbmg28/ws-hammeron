package br.com.kbmg.wsmusiccontrol.config.logging;

import br.com.kbmg.wsmusiccontrol.config.security.UserCredentialsSecurity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogMessage {
    private UserCredentialsSecurity credentials;
    private String message;
}
