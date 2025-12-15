package com.example.demo.exception;

public class RedemptionCodeAlreadyUsedException extends RuntimeException {
    public RedemptionCodeAlreadyUsedException(String message) {
        super(message);
    }
}