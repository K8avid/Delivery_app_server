package com.example.coligo.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import com.example.coligo.dto.request.TripRequestDTO;

import com.example.coligo.dto.response.TripResponseDTO;
import com.example.coligo.model.Location;
import com.example.coligo.model.Trip;
import com.example.coligo.model.User;



@Component
public class TripMapper {

    @Autowired
    LocationMapper locationMapper;

    public Trip toEntity(TripRequestDTO dto, Location startLocation, Location endLocation, User publisher) {
        return Trip.builder()
                .startLocation(startLocation)
                .endLocation(endLocation)
                .departureTime(dto.getDepartureTime())
                .distance(dto.getDistance())
                .duration(dto.getDuration())
                .polyline(dto.getPolyline())
                .publisher(publisher)
                .build();
    }


    public TripResponseDTO toResponseDTO(Trip trip) {
        TripResponseDTO dto = new TripResponseDTO();
        dto.setTripNumber(trip.getTripNumber());
        dto.setStartLocation(locationMapper.toResponseDTO(trip.getStartLocation()));
        dto.setEndLocation(locationMapper.toResponseDTO(trip.getEndLocation()));
        dto.setDepartureTime(trip.getDepartureTime());
        dto.setDistance(trip.getDistance());
        dto.setDuration(trip.getDuration());
        dto.setPolyline(trip.getPolyline());
        dto.setStatus(trip.getStatus().name());
        dto.setCreatedAt(trip.getCreatedAt());
        dto.setUpdatedAt(trip.getUpdatedAt());
        return dto;
    }

}

