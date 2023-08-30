package smu.poodle.smnavi.user.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import smu.poodle.smnavi.errorcode.CommonErrorCode;
import smu.poodle.smnavi.exception.RestApiException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        throw new RestApiException(CommonErrorCode.NO_AUTHORIZE);
    }
}
