package smu.poodle.smnavi.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import smu.poodle.smnavi.user.domain.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,String> {
    Optional<UserEntity> findByEmail(String email);

    @Query("select u from UserEntity as u where u.jwtRefreshToken.refreshToken = :refreshToken")
    Optional<UserEntity> findByRefreshToken(@Param("refreshToken") String refreshToken);
}
