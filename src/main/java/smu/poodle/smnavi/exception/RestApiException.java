package smu.poodle.smnavi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import smu.poodle.smnavi.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class RestApiException extends RuntimeException {
    private final ErrorCode errorCode;
}
