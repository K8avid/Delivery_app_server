package com.example.coligo.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LocationResponseDTO {
    private Double latitude;
    private Double longitude;
    private String address;
}

