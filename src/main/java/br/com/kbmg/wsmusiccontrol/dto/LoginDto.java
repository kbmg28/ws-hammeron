package br.com.kbmg.wsmusiccontrol.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    @Email
    private String email;
    @NotNull
    private String password;
}
