package br.com.kbmg.wsmusiccontrol.config.logging;

import br.com.kbmg.wsmusiccontrol.exception.AuthorizationException;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.util.response.ResponseData;
import br.com.kbmg.wsmusiccontrol.util.response.ResponseError;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Gson gson = new Gson();

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ResponseData<?>> handleInternal(final Exception ex, final WebRequest request) {
        return generatedError("An unexpected error has occurred", HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ResponseData<?>> handleAccessDeniedException(final AccessDeniedException ex, final WebRequest request) {
        return generatedError("Access Denied", HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler({DataAccessException.class, HibernateException.class, DataIntegrityViolationException.class})
    protected ResponseEntity<ResponseData<?>> handleConflict(final DataAccessException ex, final WebRequest request) {

        return generatedError("Database error", HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    protected ResponseEntity<ResponseData<?>> handleNotFound(final EntityNotFoundException ex, final WebRequest request) {
        return generatedError(ex.getMessage(), HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler({IllegalArgumentException.class, EntityExistsException.class})
    public ResponseEntity<ResponseData<?>> handleArguments(final RuntimeException ex, final WebRequest request) {
        return generatedError(ex.getMessage(), HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler({ServiceException.class})
    public ResponseEntity<ResponseData<?>> handleServiceException(final ServiceException ex, final WebRequest request) {
        return generatedError(ex.getMessage(), HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler({AuthorizationException.class})
    public ResponseEntity<ResponseData<?>> handleAccessDeniedException(final AuthorizationException ex, final WebRequest request) {
        return generatedError("Invalid token access", HttpStatus.UNAUTHORIZED, ex);
    }

    @ExceptionHandler({HttpMessageConversionException.class})
    public ResponseEntity<ResponseData<?>> handleHttpMessageConversionException(final RuntimeException ex, final WebRequest request) {
        return generatedError("JSON data is invalid", HttpStatus.BAD_REQUEST, ex);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status,
        final WebRequest request) {
        String msg = "Invalid Data fields: ";
        msg += ex.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(", "));
        return handleExceptionInternal(ex, new ResponseError(HttpStatus.BAD_REQUEST, msg), headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status,
        WebRequest request) {
        return handleExceptionInternal(ex, new ResponseError(HttpStatus.BAD_REQUEST, "Invalid data"), headers, HttpStatus.BAD_REQUEST, request);
    }

    private ResponseEntity<ResponseData<?>> generatedError(String message, HttpStatus http, Exception ex) {
        ResponseError responseError = new ResponseError(http, message);
        ResponseData<?> data = new ResponseData<>(null, responseError);

        log.error(gson.toJson(responseError), ex);
        return new ResponseEntity<>(data, http);
    }

}
