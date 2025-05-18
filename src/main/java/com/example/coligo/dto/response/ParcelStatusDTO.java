package com.example.coligo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

import com.example.coligo.enums.ParcelStatusType;

@Data
@AllArgsConstructor
public class ParcelStatusDTO {
    private ParcelStatusType status;
    private LocalDateTime timestamp;
}
