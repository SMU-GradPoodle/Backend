package smu.poodle.smnavi.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import smu.poodle.smnavi.common.errorcode.StatusCode;

@Getter
@RequiredArgsConstructor
public class RestApiException extends RuntimeException {
    private final StatusCode statusCode;
}
