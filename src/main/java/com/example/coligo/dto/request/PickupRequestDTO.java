package com.example.coligo.dto.request;



import lombok.Data;

@Data
public class PickupRequestDTO {
    private String pickupToken;
    private String tripNumber;
}
