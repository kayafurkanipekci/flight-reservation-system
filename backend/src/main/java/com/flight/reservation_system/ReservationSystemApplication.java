package com.flight.reservation_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @EnableCaching burada değil; prod'da CacheConfig aktif olduğunda oraya taşındı.
// Test ortamında CacheConfig devre dışı kalıyor ve Spring CacheManager kurmuyor.
public class ReservationSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(ReservationSystemApplication.class, args);
	}

}
