package com.gigajet.mhlb.exception;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Value("${logging.slack.web-hook-url}")
    private String slackBot;

    @ExceptionHandler(CustomException.class)
    @Async
    public ResponseEntity<SendMessageDto> handleCustomException(CustomException exception, HandlerMethod handlerMethod, WebRequest webRequest) {//웹리퀘스트로 요청 긁어옴
        log.error("CustomException throw Exception : {}", exception.getErrorCode());
//        StringBuilder message = new StringBuilder("Error 발생 ")
//                .append("\n\n").append(exception.getErrorCode().toString()).append("\n\n")//에러 이넘
//                .append(handlerMethod.getBeanType().getSimpleName())//컨트롤러 이름만
//                .append(" : ").append(handlerMethod.getMethod()//메소드 이름만
//                        .getName()).append(" 메소드에서 발생\n")
//                .append("\nuser-agent : ").append(webRequest.getHeader("user-agent"))
//                .append("\n유저 토큰 :").append(webRequest.getHeader("authorization"));
//        sendSlack(message.toString());
        return SendMessageDto.toResponseEntity(exception.getErrorCode());
    }
//    private void sendSlack(String message) {
//        final String url = slackBot;
//        RestTemplate restTemplate = new RestTemplate();
//        Map<String, String> slack = new HashMap<>();
//        slack.put("text", message);
//        HttpEntity<Map<String, String>> entity = new HttpEntity<>(slack);
//        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
//    }
}