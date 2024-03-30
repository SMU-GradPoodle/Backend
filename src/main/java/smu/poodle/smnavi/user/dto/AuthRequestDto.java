package smu.poodle.smnavi.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import smu.poodle.smnavi.user.domain.Authority;
import smu.poodle.smnavi.user.domain.UserEntity;

public class AuthRequestDto {

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Nickname {
        String nickname;
    }


    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Certification {
        @Pattern(regexp = "^[a-zA-Z0-9]+@sangmyung\\.kr",
                message = "상명대 메일만 사용 가능합니다.")
        String email;
        String certificationKey;

    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Login {
        @Pattern(regexp = "^[a-zA-Z0-9]+@sangmyung\\.kr",
                message = "상명대 메일만 사용 가능합니다.")
        String email;
        String password;
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SignUp {
        @Pattern(regexp = "^[a-zA-Z0-9]+@sangmyung\\.kr",
                message = "상명대 메일만 사용 가능합니다.")
        String email;
        String password;
        String nickname;
        String certificationKey;

        public UserEntity toDto(PasswordEncoder passwordEncoder) {
            return UserEntity.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .nickname(nickname)
                    .authority(Authority.ROLE_USER)
                    .build();
        }
    }
}
