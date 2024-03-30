package smu.poodle.smnavi.user.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.poodle.smnavi.common.errorcode.DetailErrorCode;
import smu.poodle.smnavi.common.exception.RestApiException;
import smu.poodle.smnavi.user.domain.UserEntity;
import smu.poodle.smnavi.user.dto.AuthRequestDto;
import smu.poodle.smnavi.user.redisdomain.MailVerificationCache;
import smu.poodle.smnavi.user.redisrepository.MailVerificationCacheRepository;
import smu.poodle.smnavi.user.repository.UserRepository;

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
            throw new RestApiException(DetailErrorCode.DUPLICATE_NICKNAME);
        });
    }

    @Transactional
    public void signup(AuthRequestDto.SignUp authRequestDto) {
        MailVerificationCache mailVerificationCache = mailVerificationCacheRepository.findById(authRequestDto.getEmail())
                .orElseThrow(() -> new RestApiException(DetailErrorCode.NOT_CERTIFICATED));

        if (!mailVerificationCache.getVerificationKey().equals(authRequestDto.getCertificationKey()) ||
                !mailVerificationCache.getIsVerify()) {
            throw new RestApiException(DetailErrorCode.NOT_CERTIFICATED);
        }

        UserEntity user = authRequestDto.toDto(passwordEncoder);

        userRepository.save(user);
    }

    @Transactional
    public void sendVerificationMail(AuthRequestDto.Certification authRequestDto) {
        userRepository.findByEmail(authRequestDto.getEmail()).ifPresent((user) -> {
            throw new RestApiException(DetailErrorCode.DUPLICATION_ERROR);
        });
        String certificationKey = emailService.sendCertificationKey(authRequestDto.getEmail());
        mailVerificationCacheRepository.save(MailVerificationCache.builder()
                .email(authRequestDto.getEmail())
                .verificationKey(certificationKey)
                .isVerify(false)
                .expiration(CERTIFICATION_KEY_EXPIRE_SECONDS)
                .build());
    }


    @Transactional
    public void authenticateMail(AuthRequestDto.Certification authRequestDto) {
        MailVerificationCache mailVerificationCache = mailVerificationCacheRepository.findById(authRequestDto.getEmail())
                .orElseThrow(() -> new RestApiException(DetailErrorCode.INVALID_CERTIFICATION_KEY));

        if (mailVerificationCache.getVerificationKey().equals(authRequestDto.getCertificationKey())) {
            mailVerificationCacheRepository.save(MailVerificationCache.builder()
                    .email(authRequestDto.getEmail())
                    .verificationKey(authRequestDto.getCertificationKey())
                    .isVerify(true)
                    .expiration(CERTIFICATION_KEY_EXPIRE_SECONDS)
                    .build());
        } else {
            throw new RestApiException(DetailErrorCode.INVALID_CERTIFICATION_KEY);
        }
    }
}
