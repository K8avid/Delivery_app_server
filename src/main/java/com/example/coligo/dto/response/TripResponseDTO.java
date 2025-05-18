package com.example.coligo.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TripResponseDTO {
    private String tripNumber;
    private LocationResponseDTO startLocation;
    private LocationResponseDTO endLocation;
    private LocalDateTime departureTime;
    private Double distance;
    private Integer duration;
    private String polyline;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

