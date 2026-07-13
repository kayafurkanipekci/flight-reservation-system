package com.flight.reservation_system.airplane;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoAirplaneRequest {

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Tail number is required")
    private String tailNumber;

    @Min(value = 1, message = "Capacity must be greater than 0")
    private Integer capacity;

    @NotBlank(message = "Airline is required")
    private String airline;
}