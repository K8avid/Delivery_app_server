package com.example.coligo.dto.request;

import com.example.coligo.enums.DeliveryStatus;

import lombok.Data;

@Data
public class DeliveryStatusUpdateDTO {
     private DeliveryStatus status;
}
