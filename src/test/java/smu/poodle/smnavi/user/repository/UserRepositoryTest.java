package smu.poodle.smnavi.user.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import smu.poodle.smnavi.user.domain.Authority;
import smu.poodle.smnavi.user.domain.UserEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static smu.poodle.smnavi.user.UserTestInstance.EMAIL;
import static smu.poodle.smnavi.user.UserTestInstance.USERENTITY;

@DataJpaTest
@ActiveProfiles({"h2"})
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("이메일로 저장된 사용자 정보를 찾을 수 있다.")
    void given_existEmail_When_findByNickname_Then_present() {
        // Given
        UserEntity user = userRepository.save(USERENTITY);

        // When
        Optional<UserEntity> optionalFoundUser = userRepository.findByEmail(user.getEmail());

        // Then
        assertThat(optionalFoundUser).isPresent();
        UserEntity foundUser = optionalFoundUser.get();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 조회를 시도하면 Empty 인스턴스를 반환한다.")
    void given_notExistEmail_When_findByNickname_Then_empty() {
        //given
        String email = EMAIL;

        // When
        Optional<UserEntity> optionalFoundUser = userRepository.findByEmail(email);

        // Then
        assertThat(optionalFoundUser).isEmpty();
    }

    @Test
    @DisplayName("닉네임으로 저장된 사용자 정보를 찾을 수 있다.")
    void given_existNickname_When_findByNickname_Then_found() {
        // Given
        UserEntity user = userRepository.save(USERENTITY);

        // When
        Optional<UserEntity> optionalFoundUser = userRepository.findByNickname(user.getNickname());

        // Then
        assertThat(optionalFoundUser).isPresent();
        UserEntity foundUser = optionalFoundUser.get();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("존재하지 않는 닉네임으로 조회를 시도하면 Empty 인스턴스를 반환한다.")
    void given_notExistNickname_When_findByNickname_Then_empty() {
        // Given
        String nickname = "testUser";

        // When
        Optional<UserEntity> optionalFoundUser = userRepository.findByNickname(nickname);

        // Then
        assertThat(optionalFoundUser).isEmpty();
    }


    @Test
    @DisplayName("중복된 이메일의 유저를 저장하려고 하면 예외가 발생한다.")
    void given_duplicatedEmailUser_When_save_Then_ThrowException() {
        // Given
        UserEntity user1 = UserEntity.builder()
                .email("201811000@sangmyung.kr")
                .password("password")
                .nickname("testUser1")
                .authority(Authority.ROLE_USER)
                .build();

        UserEntity user2 = UserEntity.builder()
                .email("201811000@sangmyung.kr")
                .password("password")
                .nickname("testUser2")
                .authority(Authority.ROLE_USER)
                .build();

        //when, then
        userRepository.save(user1);
        assertThatThrownBy(() -> userRepository.save(user2)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("중복된 닉네임의 유저를 저장하려고 하면 예외가 발생한다.")
    void given_duplicatedNicknameUser_When_save_Then_ThrowException() {
        // Given
        UserEntity user1 = UserEntity.builder()
                .email("201811000@sangmyung.kr")
                .password("password")
                .nickname("testUser")
                .authority(Authority.ROLE_USER)
                .build();

        UserEntity user2 = UserEntity.builder()
                .email("201911000@sangmyung.kr")
                .password("password")
                .nickname("testUser")
                .authority(Authority.ROLE_USER)
                .build();

        //when, then
        userRepository.save(user1);
        assertThatThrownBy(() -> userRepository.save(user2)).isInstanceOf(DataIntegrityViolationException.class);
    }
}
