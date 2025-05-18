package com.example.coligo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.coligo.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    Optional<Location> findByAddress(String address);
    boolean existsByLatitudeAndLongitude(Double latitude, Double longitude);
    Optional<Location> findByLatitudeAndLongitude(Double latitude, Double longitude);

}
