package com.flight.reservation_system.airplane;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "airplanes")
@Getter
@Setter
@NoArgsConstructor //Hibernate'in Entity'yi okuyabilmesi için zorunlu.
@AllArgsConstructor //Lombok'un sağladığı bir anotasyon. Tüm alanları parametre olarak alan bir constructor oluşturur.
// ^- "tüm alanları tek satırda doldur" kısayolu
public class Airplane {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String model;
    private String tailNumber;
    private int capacity;
    private String airline;
}
