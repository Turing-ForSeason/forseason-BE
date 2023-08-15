package com.turing.forseason.global.config.security;

import com.turing.forseason.domain.user.repository.UserRepository;
import com.turing.forseason.global.jwt.JwtRequestFilter;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Configuration 어노테이션 안붙여도 되나...?
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private UserRepository userRepository;
    public JwtSecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void configure(HttpSecurity http) {
        http.addFilterBefore(
                // UsernamePasswordAuthenticationFilter이전에 JwtRequestFilter를 거치도록 등록
                // UsernamePasswordAuthenticationFilter: 사용자의 아이디와 비밀번호를 기반으로 인증하는 역할
                new JwtRequestFilter(userRepository),
                UsernamePasswordAuthenticationFilter.class
        );
    }
}