package com.flight.reservation_system.airport;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoAirportRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "IATA code is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "IATA code must be exactly 3 uppercase letters")
    private String iataCode;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Country is required")
    private String country;
}