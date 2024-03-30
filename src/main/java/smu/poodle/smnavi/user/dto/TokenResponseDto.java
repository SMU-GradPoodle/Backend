package smu.poodle.smnavi.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import smu.poodle.smnavi.user.jwt.TokenType;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenResponseDto {
    TokenType tokenType;
    String token;
}
