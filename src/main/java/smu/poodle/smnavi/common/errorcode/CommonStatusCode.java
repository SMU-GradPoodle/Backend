package smu.poodle.smnavi.common.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CommonStatusCode implements StatusCode {
    OK(HttpStatus.OK, "C200", "요청이 성공하였습니다."),
    CREATED(HttpStatus.CREATED, "C201", "리소스가 생성되었습니다."),

    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "C400","올바르지 않은 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C401", "사용자 인증에 실패하였습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "C403", "권한이 없습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C404", "리소스가 존재하지 않습니다."),
    VALIDATION_FAILED(HttpStatus.UNPROCESSABLE_ENTITY, "C422-1", "유효성 검증에 실패하였습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C500-1", "서버 내부 에러입니다."),
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
