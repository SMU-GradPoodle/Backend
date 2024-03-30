package smu.poodle.smnavi.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Authority {
    ROLE_USER("user"), ROLE_ADMIN("admin");
    private final String roleName;
}

