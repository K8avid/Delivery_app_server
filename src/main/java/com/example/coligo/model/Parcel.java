package com.example.coligo.model;


import java.time.LocalDateTime;
import com.example.coligo.enums.ParcelStatusType;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;



@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Positive(message = "Weight must be positive")
    private Double weight; // Poids en kilogrammes

    @Column(nullable = false)
    @Positive(message = "Length must be positive")
    private Double length; // Longueur en Cm

    @Column(nullable = false)
    @Positive(message = "Width must be positive")
    private Double width; // Largeur en Cm

    @Column(nullable = false)
    @Positive(message = "Height must be positive")
    private Double height; // Hauteur en Cm

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(nullable = false)
    @Size(min = 10, message = "Pickup address must be at least 10 characters long")
    private String senderAddress;

    @Column(nullable = false)
    @Size(min = 10, message = "Delivery address must be at least 10 characters long")
    private String recipientAddress;

    @Column(nullable = false, unique = true)
    private String parcelNumber; // Code unique du colis

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParcelStatusType currentStatus;


    @Column(nullable = true, unique = true) //avant David c'etait nullable = false
    private String pickupToken;

    @Column(nullable = true, unique = true) //avant David c'etait nullable = false
    private String deliveryToken;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private LocalDateTime expiracyDate;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if ( this.currentStatus != ParcelStatusType.NO_TRIP ) {  //comme ça je gere les 2 type de parcel, ceux qui sont crée pendant delivery et ceux qui ne le sont pas
            this.currentStatus = ParcelStatusType.CREATED;
        }
        
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
