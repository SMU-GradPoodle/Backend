package smu.poodle.smnavi.user.sevice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import smu.poodle.smnavi.user.domain.UserEntity;
import smu.poodle.smnavi.user.dto.LoginRequestDto;
import smu.poodle.smnavi.user.repository.UserRepository;

@Service
public class UserSignupService {
    @Autowired
    private final UserRepository userRepository;
    public UserSignupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Autowired
    private PasswordEncoder passwordEncoder;



}
