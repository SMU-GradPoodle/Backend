package smu.poodle.smnavi.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import smu.poodle.smnavi.user.auth.Authority;

import java.util.Collection;
import java.util.Collections;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email; //이메일 = 닉네임
    @Column
    private String password; //비밀번호

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @OneToOne(mappedBy = "user")
    JwtRefreshToken jwtRefreshToken;

    public Collection<GrantedAuthority> getGrantedAuthority() {
        return Collections.singleton(new SimpleGrantedAuthority(authority.toString()));
    }

}