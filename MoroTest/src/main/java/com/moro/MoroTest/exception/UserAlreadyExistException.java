package com.moro.MoroTest.exception;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String field) {
        super("User with username " + field + " already exists.");
    }
}