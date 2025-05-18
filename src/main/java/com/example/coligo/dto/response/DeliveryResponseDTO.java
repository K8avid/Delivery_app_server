package com.example.coligo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import com.example.coligo.enums.DeliveryStatus;

@Data
@Builder
public class DeliveryResponseDTO {

    private String deliveryNumber;
    private ParcelResponseDTO parcel;
    private TripResponseDTO trip;
    private DeliveryStatus status;
    private String qrToken;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
