package com.example.coligo.mapper;

import org.springframework.stereotype.Component;

import com.example.coligo.dto.request.LocationRequestDTO;
import com.example.coligo.dto.response.LocationResponseDTO;
import com.example.coligo.model.Location;



@Component
public class LocationMapper {

    public Location toEntity(LocationRequestDTO dto, String address) {
        return Location.builder()
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .address(address)
                .build();
    }


    public LocationResponseDTO toResponseDTO(Location location) {
        return LocationResponseDTO.builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .address(location.getAddress())
                .build();
    }


}
