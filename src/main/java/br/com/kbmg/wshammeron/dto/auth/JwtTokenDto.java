package br.com.kbmg.wshammeron.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenDto {

    @NotBlank
    private String jwtToken;
}
