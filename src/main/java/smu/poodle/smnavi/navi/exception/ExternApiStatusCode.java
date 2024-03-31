package smu.poodle.smnavi.navi.exception;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import smu.poodle.smnavi.common.errorcode.StatusCode;

@RequiredArgsConstructor
public enum ExternApiStatusCode implements StatusCode {
    UNSUPPORTED_OR_INVALID_GPS_POINTS(HttpStatus.BAD_REQUEST, "E400-1", "해당 지점은 길찾기가 지원되지 않거나 잘못된 입력값입니다."),
    NO_PATH_FOUND(HttpStatus.NOT_FOUND, "E404-1", "검색된 경로가 없습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    public HttpStatus status() {
        return status;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}
