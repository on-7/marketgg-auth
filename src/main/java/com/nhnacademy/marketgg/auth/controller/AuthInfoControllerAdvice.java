package com.nhnacademy.marketgg.auth.controller;

import com.nhnacademy.marketgg.auth.dto.response.common.CommonResponse;
import com.nhnacademy.marketgg.auth.dto.response.common.ErrorEntity;
import com.nhnacademy.marketgg.auth.exception.AuthNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AuthInfoControllerAdvice {

    @ExceptionHandler({
        AuthNotFoundException.class
    })
    public ResponseEntity<CommonResponse> errorControl(Exception e) {
        log.error("", e);
        ErrorEntity error = new ErrorEntity(e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(error);
    }

}
