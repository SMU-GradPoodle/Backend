package smu.poodle.smnavi.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import smu.poodle.smnavi.common.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class RestApiException extends RuntimeException {
    private final ErrorCode errorCode;
}
