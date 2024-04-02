package smu.poodle.smnavi.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import smu.poodle.smnavi.common.exception.RestApiException;
import smu.poodle.smnavi.user.domain.UserEntity;
import smu.poodle.smnavi.user.dto.AuthRequestDto;
import smu.poodle.smnavi.user.redisdomain.MailVerificationCache;
import smu.poodle.smnavi.user.redisrepository.MailVerificationCacheRepository;
import smu.poodle.smnavi.user.repository.UserRepository;
import smu.poodle.smnavi.user.sevice.EmailService;
import smu.poodle.smnavi.user.sevice.SignupService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static smu.poodle.smnavi.user.UserTestInstance.AUTHREQUEST_VERIFICATIONMAIL;
import static smu.poodle.smnavi.user.UserTestInstance.EMAIL;

@ExtendWith(MockitoExtension.class)
public class SignupServiceTest {
    @InjectMocks
    private SignupService signupService;

    @Mock
    private EmailService emailService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MailVerificationCacheRepository mailVerificationCacheRepository;


    @Test
    @DisplayName("인증 메일 전송 - 이미 가입된 메일로 인증을 요청할 경우 예외가 발생한다.")
    void given_duplicateMail_When_sendVerificationMail_Then_throwException() {
        // given
        AuthRequestDto.VerificationMail verificationMail = AUTHREQUEST_VERIFICATIONMAIL;
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(new UserEntity()));

        // when, then
        assertThatThrownBy(() -> signupService.sendVerificationMail(verificationMail))
                        .isInstanceOf(RestApiException.class);
    }

    @Test
    @DisplayName("인증 메일 전송 - 유효한 메일로 요청하면 메일을 전송하고 인증 키를 캐싱한다.")
    void given_validMail_When_sendVerificationMail_Then_sendMailAndCachingKey() {
        // given
        AuthRequestDto.VerificationMail verificationMail = AUTHREQUEST_VERIFICATIONMAIL;
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(emailService.sendCertificationKey(EMAIL)).thenReturn(verificationMail.getVerificationKey());

        // when
        signupService.sendVerificationMail(verificationMail);

        // then
        verify(emailService, times(1)).sendCertificationKey(EMAIL);
        verify(mailVerificationCacheRepository, times(1)).save(any(MailVerificationCache.class));
    }

    @Test
    @DisplayName("메일 인증 - 인증키가 유효하면 인증 완료 상태로 업데이트한다.")
    void given_validKey_When_verifyMail_Then_updateCachingData() {
        // given
        AuthRequestDto.VerificationMail verificationMail = AUTHREQUEST_VERIFICATIONMAIL;
        when(mailVerificationCacheRepository.findById(EMAIL)).thenReturn(Optional.of(new MailVerificationCache()));

        // when, then
        signupService.verifyMail(verificationMail);
        verify(mailVerificationCacheRepository, times(1)).save(any(MailVerificationCache.class));
    }

    @Test
    void given_invalidKey_When_verifyMail_Then_throwException() {
        // given
        AuthRequestDto.VerificationMail verificationMail = AUTHREQUEST_VERIFICATIONMAIL;
        when(mailVerificationCacheRepository.findById(EMAIL)).thenReturn(Optional.of(new MailVerificationCache()));

        // when, then
        assertThatThrownBy(() -> signupService.verifyMail(verificationMail))
                .isInstanceOf(RestApiException.class);
        verify(mailVerificationCacheRepository, never()).save(any(MailVerificationCache.class));

    }
}
