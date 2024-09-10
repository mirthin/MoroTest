package com.moro.MoroTest.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String field) {
        super(field + " is required or invalid");
    }
}