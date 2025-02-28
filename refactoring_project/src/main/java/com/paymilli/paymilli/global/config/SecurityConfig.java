package com.paymilli.paymilli.global.config;

import com.paymilli.paymilli.domain.member.jwt.JwtAccessDeniedHandler;
import com.paymilli.paymilli.domain.member.jwt.JwtAuthenticationEntryPoint;
import com.paymilli.paymilli.domain.member.jwt.JwtExceptionFilter;
import com.paymilli.paymilli.domain.member.jwt.JwtFilter;
import com.paymilli.paymilli.domain.member.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final TokenProvider tokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)

            .exceptionHandling(exception -> exception
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint))

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(request -> request
                .requestMatchers("/member/check").permitAll()
                .requestMatchers("/member/join").permitAll()
                .requestMatchers("/member/login").permitAll()
                .requestMatchers("/member/refresh").permitAll()
                .requestMatchers("/member/*").hasAnyAuthority("USER", "ADMIN")
                //하단부터는 테스트용
                .requestMatchers("/user/signup").permitAll()
                .requestMatchers("/auth/authenticate").permitAll()
//                .requestMatchers("/user/user").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/user/user").hasAnyAuthority("USER", "ADMIN")
                .requestMatchers("/user/user/*").hasAnyAuthority("ADMIN")
                .requestMatchers("/error").permitAll()
                .anyRequest().authenticated())

            .addFilterBefore(new JwtExceptionFilter(tokenProvider),
                UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtFilter(tokenProvider),
                UsernamePasswordAuthenticationFilter.class)

        ;

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}