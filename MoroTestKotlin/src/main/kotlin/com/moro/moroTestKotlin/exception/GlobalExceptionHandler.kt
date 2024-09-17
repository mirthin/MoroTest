package com.moro.moroTestKotlin.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import java.nio.file.AccessDeniedException
import java.util.function.Consumer


@ControllerAdvice
class GlobalExceptionHandler {
    // Exception handler for validation errors
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String?>> {
        val errors: MutableMap<String, String?> = HashMap()

        // Iterate through the field errors
        ex.bindingResult.allErrors.forEach(Consumer { error: ObjectError ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.getDefaultMessage()
            errors[fieldName] = errorMessage
        })
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handleMissingHeader(ex: MissingRequestHeaderException?): ResponseEntity<String> {
        return ResponseEntity("Authorization header is missing or malformed", HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UserNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleUserNotFoundException(ex: UserNotFoundException): ResponseEntity<String?> {
        return ResponseEntity<String?>(ex.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UnauthorizedAccessException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleUnauthorizedAccessException(ex: UnauthorizedAccessException): ResponseEntity<String?> {
        return ResponseEntity<String?>(ex.message, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequestException(ex: BadRequestException): ResponseEntity<String?> {
        return ResponseEntity<String?>(ex.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UserAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleUserAlreadyExistsException(ex: UserAlreadyExistsException): ResponseEntity<String?> {
        return ResponseEntity<String?>(ex.message, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleBadRequestException(ex: AccessDeniedException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleBadRequestException(ex: IllegalArgumentException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGlobalException(ex: Exception): ResponseEntity<String> {
        return ResponseEntity("An unexpected error occurred: " + ex.message, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}