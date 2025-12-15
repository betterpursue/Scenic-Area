package com.example.demo.exception;

public class RedemptionCodeInvalidException extends RuntimeException {
    public RedemptionCodeInvalidException(String message) {
        super(message);
    }
}