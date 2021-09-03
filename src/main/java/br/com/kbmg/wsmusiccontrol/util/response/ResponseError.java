package br.com.kbmg.wsmusiccontrol.util.response;

import br.com.kbmg.wsmusiccontrol.constants.SwaggerConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResponseError {
    @ApiModelProperty(example = SwaggerConstants.HTTP_STATUS_EXAMPLE)
    private final String httpStatus;

    @ApiModelProperty(example = SwaggerConstants.DESCRIBE_ERROR_EXAMPLE)
    private final String message;

    public ResponseError(HttpStatus http, String message) {
        String template = "%d - %s";
        this.httpStatus = String.format(template, http.value(), http.getReasonPhrase());

        this.message = message;
    }
}
