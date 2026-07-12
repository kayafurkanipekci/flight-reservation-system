package com.flight.reservation_system.airplane;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AirplaneRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");

    @Autowired
    private AirplaneRepository airplaneRepository;

    @Test
    void shouldSaveAndRetrieveAirplane() {
        Airplane airplane = new Airplane();
        airplane.setModel("Test Airplane");
        airplane.setTailNumber("TST123");
        airplane.setCapacity(150);
        airplane.setAirline("Test Airline");

        Airplane saved = airplaneRepository.save(airplane);

        assertThat(saved.getId()).isNotNull();
        assertThat(airplaneRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void shouldNotAllowDuplicateIataCode() {
    Airplane first = new Airplane();
    first.setModel("Istanbul Airplane");
    first.setTailNumber("DUP");
    first.setCapacity(150);
    first.setAirline("Turkiye");
    airplaneRepository.saveAndFlush(first);

    Airplane duplicate = new Airplane();
    duplicate.setModel("Sahte Havalimani");
    duplicate.setTailNumber("DUP");
    duplicate.setCapacity(150);
    duplicate.setAirline("Turkiye");

    assertThatThrownBy(() -> airplaneRepository.saveAndFlush(duplicate))
            .isInstanceOf(DataIntegrityViolationException.class);
    }
    
}