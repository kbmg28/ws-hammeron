package br.com.kbmg.wshammeron.dto.user;

import br.com.kbmg.wshammeron.constants.SwaggerConstants;
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
public class RegisterPasswordDto {

    @ApiModelProperty(example = SwaggerConstants.EMAIL_EXAMPLE, required = true)
    @Email
    @NotBlank
    private String email;

    @ApiModelProperty(example = SwaggerConstants.PASSWORD_EXAMPLE, required = true)
    @NotBlank
    @Size(min = 6)
    private String password;
}
