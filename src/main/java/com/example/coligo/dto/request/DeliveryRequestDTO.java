package com.example.coligo.dto.request;

import lombok.Data;


@Data
public class DeliveryRequestDTO {
    private ParcelRequestDTO parcel; // Détails du colis à créer
    private String tripNumber;      // Numéro du trajet
}