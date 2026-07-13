package com.flight.reservation_system.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public DtoAuthResponse register(@Valid @RequestBody DtoRegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public DtoAuthResponse login(@Valid @RequestBody DtoLoginRequest request) {
        return authService.login(request);
    }
}