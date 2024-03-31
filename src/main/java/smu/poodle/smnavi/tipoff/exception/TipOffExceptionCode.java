package smu.poodle.smnavi.tipoff.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import smu.poodle.smnavi.common.errorcode.StatusCode;

@RequiredArgsConstructor
public enum TipOffExceptionCode implements StatusCode {
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
