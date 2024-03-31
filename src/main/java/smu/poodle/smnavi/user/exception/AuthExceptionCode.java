package smu.poodle.smnavi.user.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import smu.poodle.smnavi.common.errorcode.StatusCode;

@RequiredArgsConstructor
public enum AuthExceptionCode implements StatusCode {
    AUTHORIZATION_REQUIRED(HttpStatus.UNAUTHORIZED, "A401-1", "인증이 필요합니다."),
    INVALID_MAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "A401-2", "메일 혹은 비밀번호가 잘못되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A401-3", "올바르지 않은 토큰입니다."),
    REFRESH_TOKEN_NOT_EXIST(HttpStatus.UNAUTHORIZED, "A401-4", "리프레쉬 토큰이 유효하지 않습니다."),
    NOT_VERIFIED_MAIL(HttpStatus.UNAUTHORIZED, "A401-5", "인증을 진행해주세요"),
    INVALID_VERIFICATION_KEY(HttpStatus.UNAUTHORIZED, "A401-6", "잘못된 인증번호입니다."),

    EXPIRED_TOKEN(HttpStatus.FORBIDDEN, "A403-1", "토큰의 유효 시간이 만료 되었습니다."),

    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "A409-1", "이미 존재하는 닉네임입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "A409-2", "이미 가입 된 이메일입니다."),

    FAIL_TO_SEND_MAIL(HttpStatus.INTERNAL_SERVER_ERROR, "A500-1", "메일 전송에 실패하였습니다."),

    PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "T400-1", "익명 사용자는 비밀번호를 입력해야 합니다."),
    DUPLICATION_MAIL(HttpStatus.CONFLICT, "T409-1", "이미 동일 메일로 가입된 계정이 있습니다."),
    NOT_MODIFY_ERROR(HttpStatus.CONFLICT, "T409-2", "수정된 내용이 없습니다."),
    NOT_CORRECT_PASSWORD(HttpStatus.CONFLICT, "T409-3", "비밀번호가 다릅니다."),
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
