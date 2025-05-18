package com.example.coligo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.coligo.enums.TripStatus;
import com.example.coligo.model.Location;
import com.example.coligo.model.Trip;
import com.example.coligo.model.User;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long>, JpaSpecificationExecutor<Trip> {

    Optional<Trip> findByTripNumber(String tripNumber);
	long countByStartLocation(Location location);
	long countByEndLocation(Location location);
	List<Trip> findByPublisher(User publisher);
	List<Trip> findByStatus(TripStatus status);


	
    
//     List<Trip> findByDepartureTimeAfter(LocalDateTime departureTime);


//     // Rechercher les trajets proches d'une localisation (par latitude et longitude)
//     @Query("SELECT t FROM Trip t WHERE " +
//             "SQRT(POWER(t.startLocation.latitude - :latitude, 2) + POWER(t.startLocation.longitude - :longitude, 2)) < :radius")
//     List<Trip> findNearbyTrips(@Param("latitude") Double latitude, 
//                                @Param("longitude") Double longitude, 
//                                @Param("radius") Double radius);

//     // Rechercher les trajets publiés par un utilisateur spécifique
//     @Query("SELECT t FROM Trip t WHERE t.publisher.id = :publisherId")
//     List<Trip> findByPublisher(@Param("publisherId") Long publisherId);

//     // Rechercher les trajets avec une plage de date de départ
//     @Query("SELECT t FROM Trip t WHERE t.departureTime BETWEEN :startDate AND :endDate")
//     List<Trip> findByDepartureTimeRange(@Param("startDate") String startDate, 
//                                         @Param("endDate") String endDate);


//cette requete combine le fait que le depart soit dans un certain rayon 
    //pour l'adresse de depart et d'arrivé, donc ici 1 parcel 1 trip
    // ET que le depart soit avant l'expiration du parcel (date d'expiration a fournir)
    // debut pour le sender
    //fin pour le recipient
    @Query("SELECT t FROM Trip t WHERE " +
    "SQRT(POWER(t.startLocation.latitude - :latitudeDebut, 2) + POWER(t.startLocation.longitude - :longitudeDebut, 2)) < :radius " +
    "AND " +
    "SQRT(POWER(t.endLocation.latitude - :latitudeFin, 2) + POWER(t.endLocation.longitude - :longitudeFin, 2)) < :radius " +
    "AND t.departureTime <= :expiracyDate")
    List<Trip> findNearbyStartFinishTripsBeforeExpiracy(@Param("latitudeDebut") Double latitudeDebut, 
                                                 @Param("longitudeDebut") Double longitudeDebut,
                                                 @Param("latitudeFin") Double latitudeFin, 
                                                 @Param("longitudeFin") Double longitudeFin,  
                                                 @Param("radius") Double radius, 
                                                 @Param("expiracyDate") LocalDateTime expiracyDate);
              

}