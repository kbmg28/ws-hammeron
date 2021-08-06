package br.com.kbmg.wsmusiccontrol.dto;

import br.com.kbmg.wsmusiccontrol.constants.SwaggerConstants;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivateUserAccountRefreshDto {
    @ApiModelProperty(example = SwaggerConstants.EMAIL_EXAMPLE)
    @Email
    private String email;
}
