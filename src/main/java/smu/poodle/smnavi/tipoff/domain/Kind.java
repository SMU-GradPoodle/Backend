package smu.poodle.smnavi.tipoff.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import smu.poodle.smnavi.errorcode.CommonErrorCode;
import smu.poodle.smnavi.exception.RestApiException;

@Getter
@RequiredArgsConstructor
public enum Kind { //사고 종류
    DEMO(1, "시위"),
    ACCIDENT(2, "사고"),
    BUS_FULL(3, "만차"),
    BYPASS(4, "우회"),
    CATEGORY_ETC(5, "기타");
    private final int kindNumber;
    private final String kindDescription;

    public static Kind getKindByNumber(int number) {
        for (Kind kind : Kind.values()) {
            if (kind.kindNumber == number) {
                return kind;
            }
        }
        //todo: CustomException 만들기
        throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
    }
}