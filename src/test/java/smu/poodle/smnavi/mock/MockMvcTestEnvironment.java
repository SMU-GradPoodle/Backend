package smu.poodle.smnavi.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import smu.poodle.smnavi.common.exception.GlobalExceptionHandler;
import smu.poodle.smnavi.config.SecurityConfig;
import smu.poodle.smnavi.user.jwt.JwtAccessDeniedHandler;
import smu.poodle.smnavi.user.jwt.JwtAuthenticationEntryPoint;
import smu.poodle.smnavi.user.jwt.TokenProvider;

@WebMvcTest
@Import({SecurityConfig.class,
        TokenProvider.class,
        JwtAccessDeniedHandler.class,
        JwtAuthenticationEntryPoint.class})
public abstract class MockMvcTestEnvironment {
    protected static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected MockMvc mockMvc;
}
