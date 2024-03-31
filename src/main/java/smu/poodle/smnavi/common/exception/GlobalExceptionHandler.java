package smu.poodle.smnavi.common.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import smu.poodle.smnavi.common.response.BaseResponse;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RestApiException.class)
    public BaseResponse<Void> handleCustomException(RestApiException restApiException){
        return BaseResponse.fail(restApiException);
    }

//    private ResponseEntity<Object> handleExceptionInternal(StatusCode statusCode){
//        return ResponseEntity.status(statusCode.status())
//                .body(makeErrorResponse(statusCode));
//    }
//
//    private ErrorResponse makeErrorResponse(StatusCode statusCode) {
//        return ErrorResponse.builder()
//                .statusCode(statusCode.code())
//                .message(statusCode.message())
//                .build();
//    }
//    private ErrorResponse makeErrorResponse(BindException e, StatusCode statusCode) {
//        List<ErrorResponse.ValidationError> validationErrorList = e.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .map(ErrorResponse.ValidationError::of)
//                .collect(Collectors.toList());
//
//        return ErrorResponse.builder()
//                .statusCode(statusCode.code())
//                .message(statusCode.message())
//                .errors(validationErrorList)
//                .build();
//    }
}
