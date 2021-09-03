package br.com.kbmg.wsmusiccontrol.dto;

import br.com.kbmg.wsmusiccontrol.constants.SwaggerConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NotBlank
    private String name;

    @ApiModelProperty(example = SwaggerConstants.EMAIL_EXAMPLE, required = true)
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String cellPhone;

    @ApiModelProperty(example = SwaggerConstants.PASSWORD_EXAMPLE, required = true)
    @NotBlank
    @Size(min = 6)
    private String password;
}
