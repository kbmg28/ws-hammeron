package br.com.kbmg.wsmusiccontrol.util.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResponseError {
    private final String httpStatus;
    private final String message;

    public ResponseError(HttpStatus http, String message) {
        String template = "%d - %s";
        this.httpStatus = String.format(template, http.value(), http.getReasonPhrase());

        this.message = message;
    }
}
