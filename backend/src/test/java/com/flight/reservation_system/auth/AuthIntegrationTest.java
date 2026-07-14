package com.flight.reservation_system.auth;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight.reservation_system.BaseIntegrationTest;

class AuthIntegrationTest extends BaseIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void register_withValidData_shouldReturnTokenAndPassengerRole() throws Exception {
        DtoRegisterRequest request = new DtoRegisterRequest(
                "newuser@flight.com", "securePass123", "New", "User", "5551112233");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("PASSENGER"));
    }

    @Test
    void register_withInvalidEmail_shouldReturn400() throws Exception {
        DtoRegisterRequest request = new DtoRegisterRequest(
                "not-an-email", "securePass123", "New", "User", "5551112233");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_withDuplicateEmail_shouldReturn409() throws Exception {
        DtoRegisterRequest request = new DtoRegisterRequest(
                "duplicate@flight.com", "securePass123", "First", "User", "5551112233");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        DtoRegisterRequest duplicateRequest = new DtoRegisterRequest(
                "duplicate@flight.com", "anotherPass456", "Second", "User", "5554445566");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_withValidCredentials_shouldReturnToken() throws Exception {
        DtoRegisterRequest registerRequest = new DtoRegisterRequest(
                "logintest@flight.com", "myPassword1", "Login", "Test", "5559998877");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        DtoLoginRequest loginRequest = new DtoLoginRequest("logintest@flight.com", "myPassword1");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_withWrongPassword_shouldReturn401() throws Exception {
        DtoRegisterRequest registerRequest = new DtoRegisterRequest(
                "wrongpass@flight.com", "correctPassword1", "Wrong", "Pass", "5550001122");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        DtoLoginRequest loginRequest = new DtoLoginRequest("wrongpass@flight.com", "incorrectPassword");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_withNonExistentEmail_shouldReturn401() throws Exception {
        DtoLoginRequest loginRequest = new DtoLoginRequest("ghost@flight.com", "anyPassword");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessProtectedEndpoint_withoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/airports"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessProtectedEndpoint_withValidToken_shouldReturn200() throws Exception {
        DtoRegisterRequest registerRequest = new DtoRegisterRequest(
                "tokentest@flight.com", "tokenPass123", "Token", "Test", "5553334455");
        String responseBody = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(responseBody).get("token").asText();

        mockMvc.perform(get("/api/airports").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}