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
}
