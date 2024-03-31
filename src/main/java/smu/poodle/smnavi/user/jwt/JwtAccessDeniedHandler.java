package smu.poodle.smnavi.user.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import smu.poodle.smnavi.common.response.BaseResponse;
import smu.poodle.smnavi.common.exception.RestApiException;

import java.io.IOException;

import static smu.poodle.smnavi.common.errorcode.CommonStatusCode.FORBIDDEN;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        BaseResponse<Void> baseResponse = BaseResponse.fail(new RestApiException(FORBIDDEN));

        response.setStatus(baseResponse.getStatusCode().status().value());
        response.setContentType("application/json;charset=UTF-8");
        String result = mapper.writeValueAsString(baseResponse);
        response.getWriter().write(result);
    }
}
