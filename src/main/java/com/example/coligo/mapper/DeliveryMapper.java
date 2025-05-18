package com.example.coligo.mapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.coligo.dto.request.DeliveryRequestDTO;
import com.example.coligo.dto.response.DeliveryResponseDTO;
import com.example.coligo.model.Delivery;
import com.example.coligo.model.Parcel;
import com.example.coligo.model.Trip;
import com.example.coligo.model.User;



@Component
public class DeliveryMapper {

    @Autowired
    private ParcelMapper parcelMapper;

    @Autowired
    private TripMapper tripMapper;


    public DeliveryResponseDTO toResponseDTO(Delivery delivery) {
        return DeliveryResponseDTO.builder()
                .deliveryNumber(delivery.getDeliveryNumber())
                .parcel(parcelMapper.toResponseDTO(delivery.getParcel())) 
                .trip(tripMapper.toResponseDTO(delivery.getTrip())) 
                .status(delivery.getStatus()) 
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .build();
    }



    public Delivery toEntity(DeliveryRequestDTO requestDTO, Parcel parcel, Trip trip) {
        return Delivery.builder()
                .parcel(parcel)
                .trip(trip)
                .build();
    }




    public DeliveryResponseDTO toResponseDTO(Delivery delivery, User currentUser) {
        String token;
        if (delivery.getParcel().getSender().equals(currentUser)) {
            token = delivery.getParcel().getPickupToken(); // Token pour le sender
        } else if (delivery.getParcel().getReceiver().equals(currentUser)) {
            token = delivery.getParcel().getDeliveryToken(); // Token pour le receiver
        } else {
            token = null; // Pas de token pour les autres utilisateurs
        }

        // Construire le DTO
        return DeliveryResponseDTO.builder()

                .deliveryNumber(delivery.getDeliveryNumber())
                .parcel(parcelMapper.toResponseDTO(delivery.getParcel())) 
                .trip(tripMapper.toResponseDTO(delivery.getTrip())) 
                .status(delivery.getStatus()) 
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .qrToken(token)
                .build();
    }
}
