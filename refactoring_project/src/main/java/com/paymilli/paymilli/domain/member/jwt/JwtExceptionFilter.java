package com.paymilli.paymilli.domain.member.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymilli.paymilli.global.exception.BaseResponse;
import com.paymilli.paymilli.global.exception.BaseResponseStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final TokenProvider tokenProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String[] excludePath = {
            "/api/v1/paymilli/member/join",
            "/api/v1/paymilli/member/login",
            "/api/v1/paymilli/member/refresh",
            "/api/v1/paymilli/member/check"
        };

        String path = request.getRequestURI();

        return Arrays.stream(excludePath).anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws IOException, ServletException {
        try {
            log.info("input exception filter");
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            ObjectMapper objectMapper = new ObjectMapper();
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                objectMapper.writeValueAsString(new BaseResponse(BaseResponseStatus.UNAUTHORIZED)));
        }
    }
}