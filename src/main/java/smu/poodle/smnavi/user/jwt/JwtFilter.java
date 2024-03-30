package smu.poodle.smnavi.user.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = tokenProvider.getAccessToken(request);

            tokenProvider.validateToken(TokenType.ACCESS_TOKEN, token);

            Authentication authentication = tokenProvider.createAuthenticationByAccessToken(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (Exception ignored) {
        }
        //todo : 이 메서드가 뭘 의미하는지?
        filterChain.doFilter(request, response);
    }
}
