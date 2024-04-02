package smu.poodle.smnavi.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import smu.poodle.smnavi.user.domain.Authority;
import smu.poodle.smnavi.user.domain.UserEntity;

public class AuthRequestDto {
    private static final String SMU_MAIL_PATTERN =  "^[a-zA-Z0-9]+@sangmyung\\.kr";
    private static final String SMU_MAIL_MESSAGE =  "상명대 메일만 사용 가능합니다.";

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Nickname {
        public String nickname;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class VerificationMail {
        @Pattern(regexp = SMU_MAIL_PATTERN,
                message = SMU_MAIL_MESSAGE)
        String email;
        String verificationKey;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Login {
        @Pattern(regexp = SMU_MAIL_PATTERN,
                message = SMU_MAIL_MESSAGE)
        String email;
        String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SignUp {
        @Pattern(regexp = SMU_MAIL_PATTERN,
                message = SMU_MAIL_MESSAGE)
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
