package smu.poodle.smnavi.common.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import smu.poodle.smnavi.common.errorcode.StatusCode;
import smu.poodle.smnavi.common.exception.RestApiException;
import smu.poodle.smnavi.map.response.ErrorResponse;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<Object> handleCustomException(RestApiException e){
        StatusCode statusCode = e.getStatusCode();
        return handleExceptionInternal(statusCode);
    }

    private ResponseEntity<Object> handleExceptionInternal(StatusCode statusCode){
        return ResponseEntity.status(statusCode.status())
                .body(makeErrorResponse(statusCode));
    }

    private ErrorResponse makeErrorResponse(StatusCode statusCode) {
        return ErrorResponse.builder()
                .statusCode(statusCode.code())
                .message(statusCode.message())
                .build();
    }
    private ErrorResponse makeErrorResponse(BindException e, StatusCode statusCode) {
        List<ErrorResponse.ValidationError> validationErrorList = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ErrorResponse.ValidationError::of)
                .collect(Collectors.toList());

        return ErrorResponse.builder()
                .statusCode(statusCode.code())
                .message(statusCode.message())
                .errors(validationErrorList)
                .build();
    }
}
