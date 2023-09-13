package com.turing.forseason.global.config.security;



import com.turing.forseason.domain.user.repository.UserRepository;
import com.turing.forseason.global.jwt.JwtAccessDeniedHandler;
import com.turing.forseason.global.jwt.JwtAuthenticationEntryPoint;
import com.turing.forseason.global.jwt.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtSecurityConfig jwtSecurityConfig;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().configurationSource(corsConfigurationSource());

        http.csrf().disable() //CSRF 방지기능 비활성화
                // 세션 생성 X, stateless 세션 사용
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .httpBasic().disable()
                .formLogin().disable();

        // 요청에 대한 인증 및 권한 부여 설정
        // https://kauth.kakao.com/oauth/token: access 토큰 & refresh 토큰 갱신
        // https://kapi.kakao.com/v2/user/me: 사용자 정보 가져오기
        http.authorizeRequests()
                // pre-flight 요청 처리용
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                // 해당 URL에 대해 인증없이 접근 가능
                .antMatchers("/api/**", "/auth/me", "/api/login/oauth2/code/kakao",
                        "https://kauth.kakao.com/oauth/token", "https://kapi.kakao.com/v2/user/me", "/stomp/talk/**",
                        "/talk/user/delete","/signup/**", "/signin/general", "/auth/reissue","/talk/rooms",
                        "/talk/talklist","/board/boardlist").permitAll()
                // 위 URL 제외하고는 모두 인증필요
                .anyRequest().authenticated()

                .and()
                .exceptionHandling()
                .accessDeniedHandler(new JwtAccessDeniedHandler())
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())

                .and()
                .apply(jwtSecurityConfig);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // 모든 출처에서 온 요청을 허용
        corsConfiguration.addAllowedOriginPattern("*");
        // 모든 요청 헤더 허용
        corsConfiguration.addAllowedHeader("*");
        // 모든 HTTP 메서드 허용
        corsConfiguration.addAllowedMethod("*");

        // 요청과 응답 간에 인증 정보를 함께 주고 받을 수 있도록 설정
        corsConfiguration.setAllowCredentials(true);

        // 브라우저가 접근 가능한 헤더 설정
        corsConfiguration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Headers", "Authorization, x-xsrf-token, Access-Control-Allow-Headers, Origin, Accept, X-Requested-With, " +
                "Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 모든 경로에 대해 앞서 설정한 corsConfiguration를 적용
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        // 비밀번호 암호화
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
