package smu.poodle.smnavi.user.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import smu.poodle.smnavi.common.dto.BaseResponse;
import smu.poodle.smnavi.common.errorcode.CommonStatusCode;
import smu.poodle.smnavi.common.exception.RestApiException;

import java.io.IOException;


@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        BaseResponse<Void> baseResponse = BaseResponse.fail(new RestApiException(CommonStatusCode.UNAUTHORIZED));

        response.setContentType("application/json;charset=UTF-8");
        String result = mapper.writeValueAsString(baseResponse);
        response.getWriter().write(result);
    }
}
