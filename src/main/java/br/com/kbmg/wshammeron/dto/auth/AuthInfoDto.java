package br.com.kbmg.wshammeron.dto.auth;

import br.com.kbmg.wshammeron.model.UserApp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthInfoDto {

    private UserApp userApp;
    private String spaceId;
    private String spaceName;
}
