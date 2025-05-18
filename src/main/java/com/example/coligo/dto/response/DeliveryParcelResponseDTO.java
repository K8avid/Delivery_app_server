package com.example.coligo.dto.response;

import com.example.coligo.enums.DeliveryStatus;
import com.example.coligo.model.Parcel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryParcelResponseDTO {
    private String deliveryNumber;
    private Parcel parcel;
    private String senderFirstName;
    private String senderLastName;
    private String senderPhoneNumber;
    private String senderEmail;
    private DeliveryStatus deliveryStatus;
  
}
