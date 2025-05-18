package com.example.coligo.enums;

public enum DeliveryStatus {
    REQUESTED,    // La livraison a été demandée, mais le conducteur n'a pas encore accepté.
    PENDING_START, // Le conducteur a accepté, mais n'a pas encore récupéré le colis.
    IN_PROGRESS,  // Le colis est en cours de livraison.
    COMPLETED,    // La livraison est terminée.
    CANCELLED     // La livraison a été annulée.
}
