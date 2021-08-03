package br.com.kbmg.wsmusiccontrol.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenDto {
    @NotNull
    private String jwtToken;
}
