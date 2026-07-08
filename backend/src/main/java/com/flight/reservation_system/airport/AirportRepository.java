package com.flight.reservation_system.airport;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AirportRepository extends JpaRepository<Airport, Long> {
    
    /*
    interface ile AirportRepository oluşturuluyor class ile değil çünkü JPARepository'den miras aldık ve onu kullanacağız. 
    JpaRepository ile Airport entity'si (oluşturduğumuz class) ve id'sini Long seçtik (Airport.java'da öyle yapmıştık çünkü). 
    Bu sayede Airport entity'si ile ilgili CRUD işlemlerini yapabiliriz.
    Buraya bir şey eklememize gerek yok şimdilik ben de not alıyorum.
    */
}
