package com.gigajet.mhlb.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigajet.mhlb.global.common.dto.SendMessageDto;
import com.gigajet.mhlb.global.exception.CustomException;
import com.gigajet.mhlb.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();

        log.error("권한 없음");

        CustomException exception = new CustomException(ErrorCode.PERMISSION_DINED);

        response.setStatus(exception.getErrorCode().getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), SendMessageDto.of(ErrorCode.PERMISSION_DINED));
    }
}