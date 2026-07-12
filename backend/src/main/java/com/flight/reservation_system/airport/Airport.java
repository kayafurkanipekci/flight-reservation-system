package com.flight.reservation_system.airport;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/*
databse yazarken mesela iata_code şeklinde databse'de tutulması gerekiyorsa java'da iataCode şeklinde tutulması gerekiyormuş.
database     java
bigserial                    --> long
long, integer, varchar, text --> String


*/

@Entity
@Table(name = "airports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String iataCode;
    private String city;
    private String country;

}