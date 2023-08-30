package smu.poodle.smnavi.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import smu.poodle.smnavi.errorcode.ErrorCode;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseResponse<T> {
    int code;
    String message;
    T data;

    public static BaseResponse<Void> ok() {
        return BaseResponse.<Void>builder()
                .code(200)
                .message("요청이 정상적으로 수행되었습니다.")
                .build();
    }

    public static <T> BaseResponse<T> ok(T data) {
        return BaseResponse.<T>builder()
                .code(200)
                .message("요청이 정상적으로 수행되었습니다.")
                .data(data)
                .build();
    }
}
