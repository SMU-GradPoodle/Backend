package smu.poodle.smnavi.user;

import smu.poodle.smnavi.user.domain.Authority;
import smu.poodle.smnavi.user.domain.UserEntity;
import smu.poodle.smnavi.user.dto.AuthRequestDto;

public class UserTestInstance {
    public static final String EMAIL = "201811000@sangmyung.kr";
    public static final AuthRequestDto.Nickname AUTHREQUEST_NICKNAME = AuthRequestDto.Nickname.builder()
            .nickname("nickname")
            .build();

    public static final AuthRequestDto.VerificationMail AUTHREQUEST_VERIFICATIONMAIL = AuthRequestDto.VerificationMail.builder()
            .email(EMAIL)
            .verificationKey("verificationKey")
            .build();

    public static final AuthRequestDto.SignUp AUTHREQUEST_SIGNUP = AuthRequestDto.SignUp.builder()
            .email(EMAIL)
            .password("password")
            .certificationKey("certificationKey")
            .build();

    public static final UserEntity USERENTITY = UserEntity.builder()
            .email(EMAIL)
            .password("password")
            .nickname("nickname")
            .authority(Authority.ROLE_USER)
            .build();
}
