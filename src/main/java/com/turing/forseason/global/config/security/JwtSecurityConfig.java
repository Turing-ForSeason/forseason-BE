package com.turing.forseason.global.config.security;

import com.turing.forseason.domain.user.repository.UserRepository;
import com.turing.forseason.global.jwt.JwtAccessDeniedHandler;
import com.turing.forseason.global.jwt.JwtAuthenticationEntryPoint;
import com.turing.forseason.global.jwt.JwtRequestFilter;
import com.turing.forseason.global.jwt.JwtTokenProvider;
import com.turing.forseason.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Override
    public void configure(HttpSecurity http) throws Exception{
        http.addFilterBefore(new JwtRequestFilter(jwtTokenProvider, redisService), UsernamePasswordAuthenticationFilter.class);
    }
}