package smu.poodle.smnavi.user.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    public Long getLoginMemberId() {
        try {
            return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        }
        catch (NumberFormatException e) {
            return 0L;
        }
    }
    public boolean isLogIn(){
        return !SecurityContextHolder.getContext().getAuthentication().getName().isEmpty();
        //비어있으면 return false -> 로그인 하지 않은 것
    }
}

