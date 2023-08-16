package com.turing.forseason.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turing.forseason.global.dto.ApplicationErrorResponse;
import com.turing.forseason.global.errorException.CustomException;
import com.turing.forseason.global.errorException.ErrorCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        //필요한 권한이 없이 접근하려 할때 403
//        response.sendError(HttpServletResponse.SC_FORBIDDEN);

        // ApplicationErrorResponse를 이용하여 에러 응답 메세지 전송
        ApplicationErrorResponse errorResponse = new ApplicationErrorResponse(ErrorCode.INVALID_JWT_TOKEN);
        ObjectMapper objectMapper = new ObjectMapper();
        String errorJson = objectMapper.writeValueAsString(errorResponse);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(errorJson);
    }
}
