package com.example.demo.handler;

import com.example.demo.exception.RedemptionCodeAlreadyUsedException;
import com.example.demo.exception.RedemptionCodeExpiredException;
import com.example.demo.exception.RedemptionCodeInvalidException;
import com.example.demo.utils.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RedemptionCodeInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleRedemptionCodeInvalidException(RedemptionCodeInvalidException e) {
        return Result.error(400, e.getMessage());
    }
    
    @ExceptionHandler(RedemptionCodeAlreadyUsedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleRedemptionCodeAlreadyUsedException(RedemptionCodeAlreadyUsedException e) {
        return Result.error(401, e.getMessage());
    }
    
    @ExceptionHandler(RedemptionCodeExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleRedemptionCodeExpiredException(RedemptionCodeExpiredException e) {
        return Result.error(402, e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleException(Exception e) {
        return Result.error(500, "系统异常：" + e.getMessage());
    }
}