package br.com.kbmg.wshammeron.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOnlyIdNameAndEmailDto {

    private String userId;
    private String name;
    private String email;
}
