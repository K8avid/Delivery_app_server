package com.example.coligo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.coligo.dto.request.TripRequestDTO;
import com.example.coligo.dto.request.TripStatusUpdateDTO;
import com.example.coligo.dto.response.TripResponseDTO;
import com.example.coligo.service.TripService;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;






@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private TripService tripService;

  
    @GetMapping
    public ResponseEntity<List<TripResponseDTO>> getAllTrips() {
        List<TripResponseDTO> trips = tripService.getAllTrips();
        return ResponseEntity.ok(trips);
    }

   

    @GetMapping("/search3")
    public ResponseEntity<?> searchTrips_3(
            @RequestParam("departureAddress") String departureAddress,
            @RequestParam("arrivalAddress") String arrivalAddress,
            @RequestParam("dateLimit") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateLimit
    ) {
        try {
            List<TripResponseDTO> trips = tripService.searchTrips_v3(departureAddress, arrivalAddress, dateLimit);
            if (trips.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No trips found for the provided criteria.");
            }
            return ResponseEntity.ok(trips);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format. Use yyyy-MM-dd.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // /search
    @GetMapping("/search")
    public ResponseEntity<?> searchTrips(
            @RequestParam("departureAddress") String departureAddress,
            @RequestParam("arrivalAddress") String arrivalAddress,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date // Format: yyyy-MM-dd
    ) {
        try {

            List<TripResponseDTO> trips = tripService.searchTrips(departureAddress, arrivalAddress, date);
            if (trips.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No trips found for the provided criteria.");
            }

            return ResponseEntity.ok(trips);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format. Use yyyy-MM-dd.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }




    @GetMapping("/{tripNumber}")
    public ResponseEntity<TripResponseDTO> getTripByNumber(@PathVariable String tripNumber) {
        TripResponseDTO trip = tripService.getTripByNumber(tripNumber);
        return ResponseEntity.ok(trip);
    }


    @GetMapping("/my-trips")
    public ResponseEntity<List<TripResponseDTO>> getMyTrips() {
        List<TripResponseDTO> response = tripService.getTripsForCurrentAuthenticatedUser();
        return ResponseEntity.ok(response);
    }

    
    @PostMapping
    public ResponseEntity<TripResponseDTO> createTrip(@Valid @RequestBody TripRequestDTO tripRequestDTO) {
        TripResponseDTO trip = tripService.createTrip(tripRequestDTO);
        return ResponseEntity.ok(trip);
    }


    @PutMapping("/{tripNumber}")
    public ResponseEntity<TripResponseDTO> updateTrip(@PathVariable String tripNumber, @RequestBody @Valid TripRequestDTO tripRequestDTO) {
        TripResponseDTO trip = tripService.updateTrip(tripNumber, tripRequestDTO);
        return ResponseEntity.ok(trip);
    }


    @DeleteMapping("/{tripNumber}")
    public ResponseEntity<Void> deleteTrip(@PathVariable String tripNumber) {
        tripService.deleteTrip(tripNumber);
        return ResponseEntity.noContent().build();
    }
    

    @PatchMapping("/{tripNumber}/status")
    public ResponseEntity<TripResponseDTO> updateTripStatus(
            @PathVariable String tripNumber,
            @RequestBody @Valid TripStatusUpdateDTO statusUpdateDTO) {
        TripResponseDTO updatedTrip = tripService.updateTripStatus(tripNumber, statusUpdateDTO);
        return ResponseEntity.ok(updatedTrip);
    }


    
}

