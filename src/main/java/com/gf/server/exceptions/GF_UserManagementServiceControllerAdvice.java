package com.gf.server.exceptions;

import javax.security.auth.login.FailedLoginException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;

@ControllerAdvice
public class GF_UserManagementServiceControllerAdvice {
    
    @ResponseBody
    @ExceptionHandler(FailedLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    String failedLoginExceptionHandler(FailedLoginException e) {
        return e.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    String authExceptionHandler(AuthException e) {
        return e.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String entityNotFoundExceptionHandler(EntityNotFoundException e) {
        return e.getMessage();
    }
}
