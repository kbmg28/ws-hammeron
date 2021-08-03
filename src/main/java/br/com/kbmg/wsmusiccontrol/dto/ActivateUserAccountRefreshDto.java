package br.com.kbmg.wsmusiccontrol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivateUserAccountRefreshDto {
    @Email
    private String email;
}
