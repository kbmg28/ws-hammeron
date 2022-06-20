package br.com.kbmg.wshammeron.dto.user;

import br.com.kbmg.wshammeron.constants.SwaggerConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivateUserAccountRefreshDto {
    @ApiModelProperty(example = SwaggerConstants.EMAIL_EXAMPLE, required = true)
    @Email
    private String email;
}
