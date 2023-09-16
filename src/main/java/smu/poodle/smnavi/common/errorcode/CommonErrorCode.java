package smu.poodle.smnavi.common.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    //
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter included"),
    //
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    INVALID_MAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "메일 혹은 비밀번호가 잘못되었습니다."),

    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 유효 시간이 경과되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "올바르지 않은 토큰입니다."),
    REFRESH_TOKEN_NOT_EXIST(HttpStatus.UNAUTHORIZED, "리프레쉬 토큰이 존재하지 않습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not exists"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    private final HttpStatus httpStatus;
    private final String message;

}
