package com.example.coligo.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

import com.example.coligo.enums.TripStatus;





@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST) 
    @JoinColumn(name = "start_location_id", nullable = false)
    private Location startLocation;

    @ManyToOne(cascade = CascadeType.PERSIST) 
    @JoinColumn(name = "end_location_id", nullable = false)
    private Location endLocation;

    private Double distance;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable = false)
    private User publisher;

    @Lob
    private String polyline;

    @Column(nullable = false, unique = true, updatable = false)
    private String tripNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Delivery> deliveries;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}


// @Entity
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class Trip {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @ManyToOne(cascade = CascadeType.PERSIST) 
//     @JoinColumn(name = "start_location_id", nullable = false)
//     private Location startLocation;

//     @ManyToOne(cascade = CascadeType.PERSIST) 
//     @JoinColumn(name = "end_location_id", nullable = false)
//     private Location endLocation;

//     private Double distance;
//     private Integer duration;

//     @Column(nullable = false)
//     private LocalDateTime departureTime;

//     @ManyToOne
//     @JoinColumn(name = "publisher_id", nullable = false)
//     private User publisher;

//     @Lob
//     private String polyline;

//     @Column(nullable = false, unique = true)
//     private String tripNumber;



//     @Enumerated(EnumType.STRING)
//     private TripStatus status;

//     @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
//     private Set<Delivery> deliveries;


//     @Column(nullable = false, updatable = false)
//     private LocalDateTime createdAt;

//     @Column(nullable = false)
//     private LocalDateTime updatedAt;



//     @PrePersist
//     protected void onCreate() {
//         this.createdAt = LocalDateTime.now();
//         this.updatedAt = LocalDateTime.now();
//     }

//     @PreUpdate
//     protected void onUpdate() {
//         this.updatedAt = LocalDateTime.now();
//     }

// }





    // private String generateTripCode() {
    //     String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    //     int randomPart = (int) (Math.random() * 9000) + 1000;
    //     return "STN" + datePart + randomPart;
    // }
    // this.tripNumber = generateTripCode();
    // this.status = TripStatus.OPEN;
