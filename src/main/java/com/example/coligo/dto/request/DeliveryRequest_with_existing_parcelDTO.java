package com.example.coligo.dto.request;

import lombok.Data;

@Data
public class DeliveryRequest_with_existing_parcelDTO {
    private String parcelNumber; // Détails du colis à créer
    private String tripNumber;      // Numéro du trajet
    
}
