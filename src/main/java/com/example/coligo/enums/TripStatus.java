package com.example.coligo.enums;

public enum TripStatus {
    OPEN,          // Trajet disponible pour accepter des livraisons.
    IN_PROGRESS,   // Trajet en cours (le conducteur est parti).
    COMPLETED,     // Trajet terminé.
    CANCELLED,     // Trajet annulé.
    CLOSED         // Trajet fermé (n'accepte plus de livraisons).
}
