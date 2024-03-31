package smu.poodle.smnavi.user.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.poodle.smnavi.common.exception.RestApiException;
import smu.poodle.smnavi.user.domain.UserEntity;
import smu.poodle.smnavi.user.dto.AuthRequestDto;
import smu.poodle.smnavi.user.redisdomain.MailVerificationCache;
import smu.poodle.smnavi.user.redisrepository.MailVerificationCacheRepository;
import smu.poodle.smnavi.user.repository.UserRepository;

import static smu.poodle.smnavi.user.exception.AuthExceptionCode.DUPLICATE_NICKNAME;
import static smu.poodle.smnavi.user.exception.AuthExceptionCode.DUPLICATION_MAIL;
import static smu.poodle.smnavi.user.exception.AuthExceptionCode.INVALID_VERIFICATION_KEY;
import static smu.poodle.smnavi.user.exception.AuthExceptionCode.NOT_VERIFIED_MAIL;

@Service
@RequiredArgsConstructor
public class SignupService {
    private static final Long CERTIFICATION_KEY_EXPIRE_SECONDS = 10 * 60L;

    private final EmailService emailService;

    private final UserRepository userRepository;
    private final MailVerificationCacheRepository mailVerificationCacheRepository;

    private final PasswordEncoder passwordEncoder;

    public void checkDuplicateNickname(AuthRequestDto.Nickname authRequestDto) {
        userRepository.findByNickname(authRequestDto.getNickname()).ifPresent((user) -> {
            throw new RestApiException(DUPLICATE_NICKNAME);
        });
    }

    @Transactional
    public void signup(AuthRequestDto.SignUp authRequestDto) {
        MailVerificationCache mailVerificationCache = mailVerificationCacheRepository.findById(authRequestDto.getEmail())
                .orElseThrow(() -> new RestApiException(NOT_VERIFIED_MAIL));

        if (!mailVerificationCache.getVerificationKey().equals(authRequestDto.getCertificationKey()) ||
                !mailVerificationCache.getIsVerify()) {
            throw new RestApiException(NOT_VERIFIED_MAIL);
        }

        UserEntity user = authRequestDto.toDto(passwordEncoder);

        userRepository.save(user);
    }

    public void sendVerificationMail(AuthRequestDto.Certification authRequestDto) {
        userRepository.findByEmail(authRequestDto.getEmail()).ifPresent((user) -> {
            throw new RestApiException(DUPLICATION_MAIL);
        });
        String certificationKey = emailService.sendCertificationKey(authRequestDto.getEmail());
        mailVerificationCacheRepository.save(MailVerificationCache.builder()
                .email(authRequestDto.getEmail())
                .verificationKey(certificationKey)
                .isVerify(false)
                .expiration(CERTIFICATION_KEY_EXPIRE_SECONDS)
                .build());
    }


    public void authenticateMail(AuthRequestDto.Certification authRequestDto) {
        MailVerificationCache mailVerificationCache = mailVerificationCacheRepository.findById(authRequestDto.getEmail())
                .orElseThrow(() -> new RestApiException(INVALID_VERIFICATION_KEY));

        if (mailVerificationCache.getVerificationKey().equals(authRequestDto.getCertificationKey())) {
            mailVerificationCacheRepository.save(MailVerificationCache.builder()
                    .email(authRequestDto.getEmail())
                    .verificationKey(authRequestDto.getCertificationKey())
                    .isVerify(true)
                    .expiration(CERTIFICATION_KEY_EXPIRE_SECONDS)
                    .build());
        } else {
            throw new RestApiException(INVALID_VERIFICATION_KEY);
        }
    }
}
