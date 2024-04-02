package smu.poodle.smnavi.user.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import smu.poodle.smnavi.mock.MockMvcTestEnvironment;
import smu.poodle.smnavi.user.dto.AuthRequestDto;
import smu.poodle.smnavi.user.sevice.LoginService;
import smu.poodle.smnavi.user.sevice.SignupService;
import smu.poodle.smnavi.user.sevice.TokenService;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static smu.poodle.smnavi.user.UserTestInstance.AUTHREQUEST_NICKNAME;
import static smu.poodle.smnavi.user.UserTestInstance.AUTHREQUEST_SIGNUP;

@WebMvcTest(AuthController.class)
public class AuthControllerTest extends MockMvcTestEnvironment {

    @Autowired
    private AuthController authController;

    @MockBean
    private SignupService signupService;
    @MockBean
    private LoginService loginService;
    @MockBean
    private TokenService tokenService;

    @Test
    @DisplayName("닉네임 중복 검사 - 닉네임을 전달하면 200 상태코드를 반환한다.")
    void test1() throws Exception {
        AuthRequestDto.Nickname nickname = AUTHREQUEST_NICKNAME;

        doNothing().when(signupService).checkDuplicateNickname(nickname);

        mockMvc.perform(post("/api/user/check-duplicate-nickname")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nickname)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("닉네임 중복 검사 - 닉네임을 전달하지 않으면 400 상태코드를 반환한다.")
    void test2() throws Exception {
        mockMvc.perform(post("/api/user/check-duplicate-nickname")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @ParameterizedTest
    @ValueSource(strings = {"201811000@sangmyung.kr", "slf4j@sangmyung.kr"})
    @DisplayName("메일 인증 - 유효한 이메일을 전달하면 200 상태코드를 반환한다.")
    void validEmailPattern(String email) throws Exception {
        AuthRequestDto.VerificationMail verificationMail = createVerificationMailInstance(email);

        mockMvc.perform(post("/api/user/verification-mail")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verificationMail))
                )
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalidemail", "invalidemail@", "invalidemail.com", "test@sangmuny.kr"})
    @DisplayName("메일 인증 - 메일의 도메인이 올바르지 않으면 유효성 검증에 위배된다.")
    void invalidEmailDomain(String email) throws Exception {
        AuthRequestDto.VerificationMail verificationMail = createVerificationMailInstance(email);

        mockMvc.perform(post("/api/user/verification-mail")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verificationMail))
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid!@sangmyung.kr", "invalid#123@sangmyung.kr"})
    @DisplayName("메일 인증 - 메일에 올바르지 않은 문자를 포함하면 422 상태코드를 반환한다.")
    void invalidSpecialCharactersInEmail(String email) throws Exception {
        AuthRequestDto.VerificationMail verificationMail = createVerificationMailInstance(email);

        mockMvc.perform(post("/api/user/verification-mail")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verificationMail))
                )
                .andExpect(status().isUnprocessableEntity());
    }

    private AuthRequestDto.VerificationMail createVerificationMailInstance(String email) {
        return AuthRequestDto.VerificationMail.builder()
                .email(email)
                .build();
    }

    @Test
    @DisplayName("회원 가입 - 유효한 사용자 정보를 전달하면 200 상태코드를 반환한다.")
    void signup() throws Exception {
        AuthRequestDto.SignUp signUp = AUTHREQUEST_SIGNUP;

        mockMvc.perform(post("/api/user/signup")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUp))
                )
                .andExpect(status().isOk());
    }
}
