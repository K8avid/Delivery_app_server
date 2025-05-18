package com.example.coligo.dto.request;

import com.example.coligo.enums.TripStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TripStatusUpdateDTO {

    @NotNull(message = "New status is required")
    private TripStatus newStatus;
}
