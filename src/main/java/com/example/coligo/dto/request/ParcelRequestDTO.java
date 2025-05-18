package com.example.coligo.dto.request;

import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ParcelRequestDTO {

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private Double weight;

    @NotNull(message = "Length is required")
    @Positive(message = "Length must be positive")
    private Double length;

    @NotNull(message = "Width is required")
    @Positive(message = "Width must be positive")
    private Double width;

    @NotNull(message = "Height is required")
    @Positive(message = "Height must be positive")
    private Double height;

    @NotBlank(message = "Sender address is required")
    private String senderAddress;

    @NotBlank(message = "Recipient address is required")
    private String recipientAddress;

    @NotNull(message = "Receiver email is required")
    @Email(message = "Receiver email must be valid")
    private String receiverEmail;

    @NotNull(message = "ExpiracyDate is required")
    private LocalDateTime expiracyDate;
}

