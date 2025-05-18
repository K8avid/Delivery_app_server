package com.example.coligo.mapper;

import org.springframework.stereotype.Component;

import com.example.coligo.dto.request.ParcelRequestDTO;
import com.example.coligo.dto.response.ParcelResponseDTO;
import com.example.coligo.model.Parcel;
import com.example.coligo.model.User;





@Component
public class ParcelMapper {

    // Convertir un ParcelRequestDTO en entité Parcel
    public Parcel toEntity(ParcelRequestDTO requestDTO, User sender, User receiver) {
        return Parcel.builder()
                .description(requestDTO.getDescription())
                .weight(requestDTO.getWeight())
                .length(requestDTO.getLength())
                .width(requestDTO.getWidth())
                .height(requestDTO.getHeight())
                .senderAddress(requestDTO.getSenderAddress())
                .recipientAddress(requestDTO.getRecipientAddress())
                .sender(sender)
                .receiver(receiver)
                .expiracyDate(requestDTO.getExpiracyDate())
                .build();
    }

    // Convertir une entité Parcel en ParcelResponseDTO
    public ParcelResponseDTO toResponseDTO(Parcel parcel) {
        return ParcelResponseDTO.builder()
                .description(parcel.getDescription())
                .weight(parcel.getWeight())
                .length(parcel.getLength())
                .width(parcel.getWidth())
                .height(parcel.getHeight())
                .senderAddress(parcel.getSenderAddress())
                .recipientAddress(parcel.getRecipientAddress())
                .parcelNumber(parcel.getParcelNumber())
                .currentStatus(parcel.getCurrentStatus())
                .createdAt(parcel.getCreatedAt())
                .updatedAt(parcel.getUpdatedAt())
                .expiracyDate(parcel.getExpiracyDate())
                .build();
    }

    
}
