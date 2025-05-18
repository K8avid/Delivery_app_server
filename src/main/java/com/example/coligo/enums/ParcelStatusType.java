package com.example.coligo.enums;

public enum ParcelStatusType {
    NO_TRIP,         // Le colis n'a pas encore été pris en charge 
    CREATED,         // Le colis a été créé
    WAIT_PICK_UP,       // Le colis a été récupéré par le conducteur
    IN_TRANSIT,      // Le colis est en cours de livraison
    DELIVERED,       // Le colis a été livré avec succès
    CANCELLED;       // La livraison du colis a été annulée

    
    // public String getDescription() {
    //     switch (this) {
    //         case NO_TRIP:
    //             return "The parcel has not been asigned to a trip";
    //         case CREATED:
    //             return "The parcel has been created and is waiting for pickup.";
    //         case PICKED_UP:
    //             return "The parcel has been picked up by the driver.";
    //         case IN_TRANSIT:
    //             return "The parcel is in transit.";
    //         case DELIVERED:
    //             return "The parcel has been successfully delivered.";
    //         case CANCELLED:
    //             return "The delivery of the parcel has been cancelled.";
    //         default:
    //             return "Unknown status.";
    //     }
    // }

    
}
