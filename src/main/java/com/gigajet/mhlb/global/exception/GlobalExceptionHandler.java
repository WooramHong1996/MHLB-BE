package com.gigajet.mhlb.global.exception;

import com.gigajet.mhlb.global.common.dto.SendMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<SendMessageDto> handleCustomException(CustomException exception) {
        log.error("CustomException throw Exception : {}", exception.getErrorCode());
        return SendMessageDto.toResponseEntity(exception.getErrorCode());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<SendMessageDto> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exception) {
        log.error("MaxUploadSizeExceededException throw Exception : {}", exception.getMessage());
        return ResponseEntity.badRequest().body(new SendMessageDto("over max size", "E-99", 400));
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        List<FieldError> allErrors = exception.getBindingResult().getFieldErrors();

        log.error("MethodArgumentNotValidException throw Exception : {}", BAD_REQUEST);

        for (FieldError allError : allErrors) {
            String fieldName = allError.getField();

            if (errors.containsKey(fieldName) && errors.get(fieldName).equals("blank")) {
                continue;
            }

            errors.put(fieldName, allError.getDefaultMessage());
            log.error(fieldName + " : " + allError.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(errors);
    }

}