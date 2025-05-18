package com.example.coligo.dto.request;


import lombok.Data;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Data
public class TripRequestDTO {

    @NotNull(message = "Start location is required")
    private LocationRequestDTO startLocation; // DTO pour représenter la localisation de départ

    @NotNull(message = "End location is required")
    private LocationRequestDTO endLocation; // DTO pour représenter la localisation de destination

    @NotNull(message = "Departure time is required")
    @Future(message = "Departure time must be in the future") // Vérifie que la date est dans le futur
    private LocalDateTime departureTime;

    @NotNull(message = "Distance is required")
    @Positive(message = "Distance must be positive")
    private Double distance; // Distance en kilomètres (doit être positive)

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    private Integer duration; // Durée en minutes (doit être positive)

    @Size(max = 5000, message = "Polyline must not exceed 5000 characters") // Limite la longueur de la polyline
    private String polyline; // Représentation de l'itinéraire en polyligne
}
