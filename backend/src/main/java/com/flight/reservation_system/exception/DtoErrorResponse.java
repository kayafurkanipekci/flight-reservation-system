package com.flight.reservation_system.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DtoErrorResponse {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
}