package com.moro.MoroTest.exception;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException() {
        super("You do not have permission to perform this action");
    }
}