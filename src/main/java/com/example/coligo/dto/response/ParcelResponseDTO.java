package com.example.coligo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import com.example.coligo.enums.ParcelStatusType;

import java.util.List;

@Data
@Builder
public class ParcelResponseDTO {

    private String description;
    private Double weight;
    private Double length;
    private Double width;
    private Double height;
    private String senderAddress;
    private String recipientAddress;
    private String parcelNumber;
    private ParcelStatusType currentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiracyDate; 
    private List<ParcelStatusDTO> statusHistory;
    
}



// {
//     "id": 1,
//     "description": "A package containing books",
//     "weight": 5.0,
//     "length": 30.0,
//     "width": 20.0,
//     "height": 10.0,
//     "pickupAddress": "123 Main Street",
//     "deliveryAddress": "456 Elm Street",
//     "parcelNumber": "SPN202412240001",
//     "currentStatus": "IN_TRANSIT",
//     "createdAt": "2024-12-23T10:00:00",
//     "updatedAt": "2024-12-24T12:00:00",
//     "statusHistory": [
//         {
//             "status": "CREATED",
//             "timestamp": "2024-12-23T10:00:00"
//         },
//         {
//             "status": "IN_TRANSIT",
//             "timestamp": "2024-12-24T12:00:00"
//         }
//     ]
// }

