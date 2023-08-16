package com.turing.forseason.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private final UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 필터링 작업 구현

        System.out.println(request.getRequestURI());

        // 헤더정보로부터 JWT 가져오기
        String jwtHeader = ((HttpServletRequest)request).getHeader(JwtProperties.HEADER_STRING);
        System.out.println(jwtHeader+"4");

        // 해당 헤더필드가 비어있다면 || bearer가 아니라면
        if(jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            // 다음 필터체인을 진행
            filterChain.doFilter(request, response);
            return;
        }

        // JWT 토근 값만 뽑기
        String token = jwtHeader.replace(JwtProperties.TOKEN_PREFIX, "");

        Long userId = null;
        String username = null;

        try {
            // 토큰 검증 (이게 인증이기 때문에 AuthenticationManager도 필요 없음)
            userId = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
                    .getClaim("id").asLong();
            username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
                    .getClaim("nickname").asString();
            System.out.println(userId+"1");
            System.out.println(username+"2");

            // 위 userId로 사용자 찾기
            UserEntity user = userRepository.findByUserId(userId).orElseThrow(
                    () -> new NoSuchElementException("해당 사용자가 존재하지 않습니다.")
            );

            PrincipalDetails principalDetails = new PrincipalDetails(user);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            principalDetails, //나중에 컨트롤러에서 DI해서 쓸 때 사용하기 편함.
                            null, // 패스워드는 모르니까 null 처리, 어차피 지금 인증하는게 아니니까!!
                            principalDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (TokenExpiredException e) {
            e.printStackTrace();
            request.setAttribute(JwtProperties.HEADER_STRING, "토큰이 만료되었습니다.");
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            request.setAttribute(JwtProperties.HEADER_STRING, "유효하지 않은 토큰입니다.");
        }

        request.setAttribute("userCode", userId);
        System.out.println("T.T");

        filterChain.doFilter(request, response);
    }
}