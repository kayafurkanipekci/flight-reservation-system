package com.flight.reservation_system.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DtoAuthResponse {
    private String token;
    private String email;
    private String role;
}