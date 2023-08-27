package smu.poodle.smnavi.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DetailErrorCode implements ErrorCode{
    DUPLICATION_ERROR(HttpStatus.CONFLICT, "이미 등록된 글입니다."),
    NOT_MODIFY_ERROR(HttpStatus.CONFLICT, "수정된 내용이 없습니다.");



    private final HttpStatus httpStatus;
    private final String message;
}
