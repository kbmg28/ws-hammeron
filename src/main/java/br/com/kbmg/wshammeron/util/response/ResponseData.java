package br.com.kbmg.wshammeron.util.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @param <T> - The data type for return in {@link org.springframework.http.ResponseEntity}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData<T> {

    private T content;
    private ResponseError error;

    public ResponseData(T content) {
        this.content = content;
    }
}
