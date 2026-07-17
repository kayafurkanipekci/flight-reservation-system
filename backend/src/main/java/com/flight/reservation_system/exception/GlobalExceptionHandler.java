package com.flight.reservation_system.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.flight.reservation_system.auth.InvalidCredentialsException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class) // ExceptionHandler -> bu tipte bir hata gelirse, bu metodu çalıştır
    // MethodArgumentNotValidException @Valid bir kural ihlali bulduğunda tam olarak bu exception'ı fırlatıyor.
    // ex.getBindingResult().getFieldErrors() ile "hangi alan, hangi mesajla başarısız oldu" listesini çekip birleştiriyoruz
    // örneğin "name: Name is required" gibi bir mesaj üretecek.
    public ResponseEntity<DtoErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        DtoErrorResponse error = new DtoErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class) // Spring Data JPA, bir veritabanı constraint'i (UNIQUE, NOT NULL, FOREIGN KEY)
    // ihlal edildiğinde bu exception'ı fırlatır
    public ResponseEntity<DtoErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        DtoErrorResponse error = new DtoErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                "A record with this value already exists.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<DtoErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        DtoErrorResponse error = new DtoErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class) // Spring Security, kullanıcı yetkisi olmayan bir endpoint'e erişmeye çalıştığında.
    public ResponseEntity<DtoErrorResponse> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex) {
        DtoErrorResponse error = new DtoErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "You do not have permission to perform this action.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(com.flight.reservation_system.exception.ResourceNotFoundException.class)
    public ResponseEntity<DtoErrorResponse> handleResourceNotFound(com.flight.reservation_system.exception.ResourceNotFoundException ex) {
        DtoErrorResponse error = new DtoErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DtoErrorResponse> handleRuntimeException(RuntimeException ex) {
        DtoErrorResponse error = new DtoErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }


    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<DtoErrorResponse> handleIllegalState(IllegalStateException ex) {
        DtoErrorResponse error = new DtoErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class) // exception, Java'da meydana gelen tüm hataların temel sınıfıdır.
    // Bu exception, programın çalışması sırasında ortaya çıkan herhangi bir hatayı temsil eder. (Diğerlerinen kaçıp gelen)
    public ResponseEntity<DtoErrorResponse> handleGenericException(Exception ex) {
        DtoErrorResponse error = new DtoErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}