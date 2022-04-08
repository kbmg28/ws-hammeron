package br.com.kbmg.wshammeron.controller;

import br.com.kbmg.wshammeron.util.response.ResponseData;
import org.springframework.http.ResponseEntity;

/**
 * This abstract class should be extended by every Controller class
 */
public abstract class GenericController {

    /**
     * Create a {@link ResponseEntity}.
     * It will contains a 200 HTTP Status (OK) no body.
     *
     * @return a {@link ResponseEntity} with content empty
     */
    protected ResponseEntity<ResponseData<Void>> ok() {
        return ResponseEntity.ok(new ResponseData<>());
    }

    /**
     * Create a {@link ResponseEntity}.
     * It will contains a 200 HTTP Status (OK) with body.
     *
     * @param data body of content from {@link ResponseData}
     * @param <T> the data type class
     *
     * @return a {@link ResponseEntity}
     */
    protected <T> ResponseEntity<ResponseData<T>> ok(T data) {
        return ResponseEntity.ok(new ResponseData<>(data));
    }

}
