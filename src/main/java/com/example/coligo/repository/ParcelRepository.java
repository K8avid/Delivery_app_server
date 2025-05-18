package com.example.coligo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.coligo.enums.ParcelStatusType;
import com.example.coligo.model.Parcel;
import com.example.coligo.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel, Long> {
    
    //Parcel findByParcelNumber(String parcelNumber);
    List<Parcel> findByCurrentStatus(ParcelStatusType status);
    List<Parcel> findBySenderOrReceiver(User sender, User receiver);


    Optional<Parcel> findByParcelNumber(String parcelNumber);

    List<Parcel> findBySenderAndCurrentStatus(User sender, ParcelStatusType currentStatus);

    // List<Parcel> findByCurrentStatus(ParcelStatusType currentStatus);




    @Query("SELECT p FROM Parcel p WHERE p.expiracyDate <= :currentDate AND p.expiracyDate > :defaultDate")
    List<Parcel> findExpiredParcels(@Param("currentDate") LocalDateTime currentDate, 
                                    @Param("defaultDate") LocalDateTime defaultDate);

    
}
