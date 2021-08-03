package br.com.kbmg.wsmusiccontrol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenHashDto {
    @Email
    private String email;

    @NotBlank
    private String tokenHash;
}
