package com.example.coligo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.coligo.dto.request.LocationRequestDTO;
import com.example.coligo.dto.response.LocationResponseDTO;
import com.example.coligo.mapper.LocationMapper;
import com.example.coligo.model.Location;
import com.example.coligo.repository.LocationRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// @Service
// public class LocationService {
//     @Autowired
//     private LocationRepository locationRepository;

//     @Autowired
//     private LocationMapper locationMapper;


//     public List<LocationResponseDTO> getAllLocations() {
//         return locationRepository.findAll().stream()
//                 .map(locationMapper::toResponseDTO)
//                 .collect(Collectors.toList());
//     }


//     public LocationResponseDTO getLocationById(Long id) {
//         Location location = locationRepository.findById(id).orElseThrow(() -> new RuntimeException("Location not found"));
//         return locationMapper.toResponseDTO(location);
//     }


    
//     // public List<LocationResponseDTO> getLocationsByName(String name) {
//     //     return locationRepository.findByNameContainingIgnoreCase(name).stream()
//     //             .map(locationMapper::toResponseDTO)
//     //             .collect(Collectors.toList());
//     // }

//     public Location createLocation(LocationRequestDTO locationRequestDTO) {
//         String address = locationRequestDTO.getAddress(); //ici on doit verifier l'address avec le GoogleMapService
//         Location location = locationMapper.toEntity(locationRequestDTO,address);
//         Location savedLocation = locationRepository.save(location);
//         return savedLocation;
//     }

    

//     @Transactional
//     public Location getOrCreateLocation(LocationRequestDTO locationRequestDTO) {
//         return locationRepository.findByLatitudeAndLongitude(
//             locationRequestDTO.getLatitude(), locationRequestDTO.getLongitude()
//         ).orElseGet(() -> createLocation(locationRequestDTO));
//     }



    
//     public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
//         final int R = 6371; 
//         double latDistance = Math.toRadians(lat2 - lat1);
//         double lonDistance = Math.toRadians(lon2 - lon1);

//         double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
//                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
//                 * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

//         double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//         return R * c; // Distance en kilomètres
//     }

    


//     public void deleteLocation(Long locationId) {
//         if (!locationRepository.existsById(locationId)) {
//             throw new RuntimeException("Location not found");
//         }
//         locationRepository.deleteById(locationId);
//     }
    
// }






@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private LocationMapper locationMapper;


    public List<LocationResponseDTO> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(locationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }


    public LocationResponseDTO getLocationById(Long id) {
        Location location = locationRepository.findById(id).orElseThrow(() -> new RuntimeException("Location not found"));
        return locationMapper.toResponseDTO(location);
    }


    
    // public List<LocationResponseDTO> getLocationsByName(String name) {
    //     return locationRepository.findByNameContainingIgnoreCase(name).stream()
    //             .map(locationMapper::toResponseDTO)
    //             .collect(Collectors.toList());
    // }

    public Location createLocation(LocationRequestDTO locationRequestDTO) {
        String address = locationRequestDTO.getAddress(); //ici on doit verifier l'address avec le GoogleMapService
        Location location = locationMapper.toEntity(locationRequestDTO,address);
        Location savedLocation = locationRepository.save(location);
        return savedLocation;
    }

    

    @Transactional
    public Location getOrCreateLocation(LocationRequestDTO locationRequestDTO) {
        return locationRepository.findByLatitudeAndLongitude(
            locationRequestDTO.getLatitude(), locationRequestDTO.getLongitude()
        ).orElseGet(() -> createLocation(locationRequestDTO));
    }



    

    
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; 
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance en kilomètres
    }

    


    public void deleteLocation(Long locationId) {
        if (!locationRepository.existsById(locationId)) {
            throw new RuntimeException("Location not found");
        }
        locationRepository.deleteById(locationId);
    }
    
}
