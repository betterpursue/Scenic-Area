package com.example.demo.exception;

public class RedemptionCodeExpiredException extends RuntimeException {
    public RedemptionCodeExpiredException(String message) {
        super(message);
    }
}