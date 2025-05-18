package com.example.coligo.dto.request;

import lombok.Data;

@Data
public class QrTokenRequestDTO {
    private String QrToken; // Token scanné
    private String tripNumber;    // Numéro du trajet
}
