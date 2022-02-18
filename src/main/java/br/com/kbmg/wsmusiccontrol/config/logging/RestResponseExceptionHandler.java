package br.com.kbmg.wsmusiccontrol.config.logging;

import br.com.kbmg.wsmusiccontrol.config.messages.MessagesService;
import br.com.kbmg.wsmusiccontrol.exception.AuthorizationException;
import br.com.kbmg.wsmusiccontrol.exception.BadUserInfoException;
import br.com.kbmg.wsmusiccontrol.exception.ForbiddenException;
import br.com.kbmg.wsmusiccontrol.exception.LockedClientException;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.util.response.ResponseData;
import br.com.kbmg.wsmusiccontrol.util.response.ResponseError;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
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

import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.ARGUMENTS_INVALID;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.DATA_FIELDS_INVALID;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.DATA_INVALID;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.ERROR_401_DEFAULT;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.ERROR_403_DEFAULT;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.ERROR_409_DEFAULT;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.ERROR_422_DEFAULT;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.ERROR_500_DEFAULT;

@Slf4j
@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Gson gson = new Gson();

    @Autowired
    public MessagesService messagesService;

    @Autowired
    public LogService logService;

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ResponseData<?>> handleInternal(final Exception ex, final WebRequest request) {
        return generatedError(messagesService.get(ERROR_500_DEFAULT), HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ResponseData<?>> handleAccessDeniedException(final AccessDeniedException ex, final WebRequest request) {
        return generatedError(messagesService.get(ERROR_403_DEFAULT), HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<ResponseData<?>> handleForbiddenException(final ForbiddenException ex, final WebRequest request) {
        return generatedError(ex.getMessage(), HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler({DataAccessException.class, HibernateException.class, DataIntegrityViolationException.class})
    protected ResponseEntity<ResponseData<?>> handleConflict(final DataAccessException ex, final WebRequest request) {

        return generatedError(messagesService.get(ERROR_409_DEFAULT), HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    protected ResponseEntity<ResponseData<?>> handleNotFound(final EntityNotFoundException ex, final WebRequest request) {
        return generatedError(ex.getMessage(), HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler({IllegalArgumentException.class,
            EntityExistsException.class,
            BadUserInfoException.class})
    public ResponseEntity<ResponseData<?>> handleArguments(final RuntimeException ex, final WebRequest request) {
        return generatedError(ex.getMessage(), HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler({ServiceException.class})
    public ResponseEntity<ResponseData<?>> handleServiceException(final ServiceException ex, final WebRequest request) {
        return generatedError(ex.getMessage(), HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler({AuthorizationException.class})
    public ResponseEntity<ResponseData<?>> handleAccessDeniedException(final AuthorizationException ex, final WebRequest request) {
        String mes = ex.getMessage() == null ? messagesService.get(ERROR_401_DEFAULT) : ex.getMessage();
        return generatedError(mes, HttpStatus.UNAUTHORIZED, ex);
    }

    @ExceptionHandler({LockedClientException.class})
    public ResponseEntity<ResponseData<?>> handleLockedClientException(final LockedClientException ex, final WebRequest request) {
        String mes = ex.getMessage() == null ? messagesService.get(ERROR_422_DEFAULT) : ex.getMessage();
        return generatedError(mes, HttpStatus.LOCKED, ex);
    }

    @ExceptionHandler({HttpMessageConversionException.class})
    public ResponseEntity<ResponseData<?>> handleHttpMessageConversionException(final RuntimeException ex, final WebRequest request) {
        return generatedError(messagesService.get(ARGUMENTS_INVALID), HttpStatus.BAD_REQUEST, ex);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status,
        final WebRequest request) {
        String msg = messagesService.get(DATA_FIELDS_INVALID);
        String errors = ex.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(", "));
        ResponseError responseError = new ResponseError(HttpStatus.BAD_REQUEST, String.format("%s %s", msg, errors));
        logService.logExceptionWithStackTraceFilter(ex, responseError);
        return handleExceptionInternal(ex, responseError, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status,
        WebRequest request) {
        ResponseError responseError = new ResponseError(HttpStatus.BAD_REQUEST, messagesService.get(DATA_INVALID));

        logService.logExceptionWithStackTraceFilter(ex, responseError);

        return handleExceptionInternal(ex, responseError, headers, HttpStatus.BAD_REQUEST, request);
    }

    private ResponseEntity<ResponseData<?>> generatedError(String message, HttpStatus http, Exception ex) {
        ResponseError responseError = new ResponseError(http, message);
        ResponseData<?> data = new ResponseData<>(null, responseError);

        logService.logExceptionWithStackTraceFilter(ex, responseError);

        return new ResponseEntity<>(data, http);
    }

}
