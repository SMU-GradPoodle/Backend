package smu.poodle.smnavi.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DetailErrorCode implements ErrorCode{
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입 된 이메일입니다."),
    DUPLICATION_ERROR(HttpStatus.CONFLICT, "이미 등록된 글입니다."),
    NOT_MODIFY_ERROR(HttpStatus.CONFLICT, "수정된 내용이 없습니다."),
    NOT_CERTIFICATED(HttpStatus.UNAUTHORIZED, "인증을 진행해주세요"),
    INVALID_CERTIFICATION_KEY(HttpStatus.UNAUTHORIZED, "잘못된 인증번호입니다."),
    FAIL_TO_SEND_MAIL(HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송에 실패하였습니다."),
    ;


    private final HttpStatus httpStatus;
    private final String message;
}
