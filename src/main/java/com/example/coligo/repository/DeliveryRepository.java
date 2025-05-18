package com.example.coligo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.coligo.enums.DeliveryStatus;
import com.example.coligo.model.Delivery;
import com.example.coligo.model.Parcel;
import com.example.coligo.model.Trip;
import com.example.coligo.model.User;

import java.util.List;
import java.util.Optional;



// @Repository
// public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

//     List<Delivery> findByStatus(DeliveryStatus status);
//     List<Delivery> findByParcel(Parcel parcel);
//     List<Delivery> findByTrip(Trip trip);
//     Optional<Delivery> findByDeliveryNumber(String deliveryNumber);

//     // Trouver les livraisons où l'utilisateur est sender ou receiver
//     @Query("SELECT d FROM Delivery d WHERE d.parcel.sender = :user OR d.parcel.receiver = :user")
//     List<Delivery> findBySenderOrReceiver(@Param("user") User user);

//     // Trouver les livraisons associées aux trajets publiés par l'utilisateur
//     @Query("SELECT d FROM Delivery d WHERE d.trip.publisher = :user")
//     List<Delivery> findByTripPublisher(@Param("user") User user);


//      // Rechercher une livraison par le numéro de colis
//      Optional<Delivery> findByParcelParcelNumber(String parcelNumber);
    


//      @Query("SELECT d.parcel FROM Delivery d WHERE d.trip.tripNumber = :tripNumber AND d.status = :status")
//      List<Parcel> findParcelsByTripNumberAndStatus(@Param("tripNumber") String tripNumber, @Param("status") DeliveryStatus status);




//     @Query("SELECT d.parcel FROM Delivery d WHERE d.trip.tripNumber = :tripNumber AND d.status NOT IN (:excludedStatuses)")
//     List<Parcel> findParcelsByTripNumberAndExcludedStatuses(
//         @Param("tripNumber") String tripNumber, 
//         @Param("excludedStatuses") List<DeliveryStatus> excludedStatuses
//     );


//     @Query("SELECT d FROM Delivery d WHERE d.trip.tripNumber = :tripNumber AND d.status = :status")
//     List<Delivery> findByTripNumberAndStatus(@Param("tripNumber") String tripNumber, @Param("status") DeliveryStatus status);



//     @Query("SELECT d FROM Delivery d WHERE d.trip.tripNumber = :tripNumber AND d.status NOT IN (:excludedStatuses)")
//     List<Delivery> findByTripNumberAndExcludedStatuses(@Param("tripNumber") String tripNumber, @Param("excludedStatuses") List<DeliveryStatus> excludedStatuses);


   
//     @Query("SELECT d FROM Delivery d " +
//     "JOIN d.parcel p " +
//     "JOIN d.trip t " +
//     "WHERE p.pickupToken = :pickupToken AND t.tripNumber = :tripNumber")
// Delivery findByParcelPickupTokenAndTripNumber(@Param("pickupToken") String pickupToken, 
//                                            @Param("tripNumber") String tripNumber);



//         @Query("SELECT d FROM Delivery d " +
//         "JOIN d.parcel p " +
//         "JOIN d.trip t " +
//         "WHERE p.deliveryToken = :deliveryToken AND t.tripNumber = :tripNumber")
// Delivery findByParcelDeliveryTokenAndTripNumber(@Param("deliveryToken") String deliveryToken,
//                                                 @Param("tripNumber") String tripNumber);

// }



@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    List<Delivery> findByStatus(DeliveryStatus status);
    List<Delivery> findByParcel(Parcel parcel);
    List<Delivery> findByTrip(Trip trip);
    Optional<Delivery> findByDeliveryNumber(String deliveryNumber);

    // Trouver les livraisons où l'utilisateur est sender ou receiver
    @Query("SELECT d FROM Delivery d WHERE d.parcel.sender = :user OR d.parcel.receiver = :user")
    List<Delivery> findBySenderOrReceiver(@Param("user") User user);

    // Trouver les livraisons associées aux trajets publiés par l'utilisateur
    @Query("SELECT d FROM Delivery d WHERE d.trip.publisher = :user")
    List<Delivery> findByTripPublisher(@Param("user") User user);


     // Rechercher une livraison par le numéro de colis
     Optional<Delivery> findByParcelParcelNumber(String parcelNumber);
    


     @Query("SELECT d.parcel FROM Delivery d WHERE d.trip.tripNumber = :tripNumber AND d.status = :status")
     List<Parcel> findParcelsByTripNumberAndStatus(@Param("tripNumber") String tripNumber, @Param("status") DeliveryStatus status);




    @Query("SELECT d.parcel FROM Delivery d WHERE d.trip.tripNumber = :tripNumber AND d.status NOT IN (:excludedStatuses)")
    List<Parcel> findParcelsByTripNumberAndExcludedStatuses(
        @Param("tripNumber") String tripNumber, 
        @Param("excludedStatuses") List<DeliveryStatus> excludedStatuses
    );


    @Query("SELECT d FROM Delivery d WHERE d.trip.tripNumber = :tripNumber AND d.status = :status")
    List<Delivery> findByTripNumberAndStatus(@Param("tripNumber") String tripNumber, @Param("status") DeliveryStatus status);



    @Query("SELECT d FROM Delivery d WHERE d.trip.tripNumber = :tripNumber AND d.status NOT IN (:excludedStatuses)")
    List<Delivery> findByTripNumberAndExcludedStatuses(@Param("tripNumber") String tripNumber, @Param("excludedStatuses") List<DeliveryStatus> excludedStatuses);


   
    @Query("SELECT d FROM Delivery d " +
    "JOIN d.parcel p " +
    "JOIN d.trip t " +
    "WHERE p.pickupToken = :pickupToken AND t.tripNumber = :tripNumber")
Delivery findByParcelPickupTokenAndTripNumber(@Param("pickupToken") String pickupToken, 
                                           @Param("tripNumber") String tripNumber);



        @Query("SELECT d FROM Delivery d " +
        "JOIN d.parcel p " +
        "JOIN d.trip t " +
        "WHERE p.deliveryToken = :deliveryToken AND t.tripNumber = :tripNumber")
Delivery findByParcelDeliveryTokenAndTripNumber(@Param("deliveryToken") String deliveryToken,
                                                @Param("tripNumber") String tripNumber);

}
