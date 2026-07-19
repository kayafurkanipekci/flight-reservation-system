package com.flight.reservation_system.security;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        // System.out.println("JwtAuthenticationFilter hit: " + request.getMethod() + " " + request.getRequestURI());
        log.info("JwtAuthenticationFilter hit: {} {}", request.getMethod(), request.getRequestURI());

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("No Bearer token for {} {}", request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // Token'in gerçekten decode edilecek kısmı boş mu? (maskelenmiş şekilde logla)
        String masked = token.length() > 12
                ? token.substring(0, 6) + "..." + token.substring(token.length() - 6)
                : token;
        log.info("Authorization header token (masked)={} uri={}", masked, request.getRequestURI());

        boolean valid;
        try {
            valid = jwtService.isTokenValid(token);
        } catch (Exception e) {
            log.warn("JWT validation exception for {}: {}", request.getRequestURI(), e.toString());
            valid = false;
        }

        log.warn("JWT valid={} for {}", valid, request.getRequestURI());

        if (valid) {
            try {
                String email = jwtService.extractEmail(token);
                String role = jwtService.extractRole(token);

                log.warn("JWT claims: email={}, role={} for {}", email, role, request.getRequestURI());

                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                var authToken = new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception e) {
                log.warn("JWT claim extraction exception for {}: {}", request.getRequestURI(), e.toString());
            }
        }

        filterChain.doFilter(request, response);
    }
}

