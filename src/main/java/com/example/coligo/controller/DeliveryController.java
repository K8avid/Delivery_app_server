package com.example.coligo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.coligo.dto.request.DeliveryRequestDTO;
import com.example.coligo.dto.request.DeliveryRequest_with_existing_parcelDTO;
import com.example.coligo.dto.request.DeliveryStatusUpdateDTO;
import com.example.coligo.dto.request.PickupRequestDTO;
import com.example.coligo.dto.request.QrTokenRequestDTO;
import com.example.coligo.dto.response.DeliveryParcelResponseDTO;
import com.example.coligo.dto.response.DeliveryResponseDTO;
import com.example.coligo.enums.DeliveryStatus;
import com.example.coligo.service.DeliveryService;

import jakarta.validation.Valid;
import java.util.List;



@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;


   
    @PostMapping("/pickup")
    public ResponseEntity<String> pickupParcel(@RequestBody PickupRequestDTO request) {
        try {
            boolean isValid = deliveryService.verifyAndPickupParcel(request.getPickupToken(), request.getTripNumber());
            if (isValid) {
                return ResponseEntity.ok("Colis récupéré avec succès !");
            } else {
                return ResponseEntity.badRequest().body("Le colis ou le trajet est invalide.");
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé : " + e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récupération : " + e.getMessage());
        }
    }



     @PostMapping("/deliver")
    public ResponseEntity<String> deliverParcel(@RequestBody QrTokenRequestDTO request) {
        try {
            boolean isValid = deliveryService.verifyAndDeliverParcel(request);
            if (isValid) {
                return ResponseEntity.ok("Colis livré avec succès !");
            } else {
                return ResponseEntity.badRequest().body("Le colis ou le trajet est invalide.");
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé : " + e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la livraison : " + e.getMessage());
        }
    }


    
    // Endpoint pour récupérer toutes les livraisons
    @GetMapping
    public ResponseEntity<List<DeliveryResponseDTO>> getAllDeliveries() {
        List<DeliveryResponseDTO> deliveries = deliveryService.getAllDeliveries();
        return ResponseEntity.ok(deliveries);
    }
    
    // Endpoint pour récupérer une livraison par son numéro unique
    @GetMapping("/{deliveryNumber}")
    public ResponseEntity<DeliveryResponseDTO> getDeliveryByNumber(@PathVariable String deliveryNumber) {
        DeliveryResponseDTO responseDTO = deliveryService.getDeliveryByNumber(deliveryNumber);
        return ResponseEntity.ok(responseDTO);
    }


    // Endpoint pour récupérer une livraison par le numéro de colis
    @GetMapping("/parcel/{parcelNumber}")
    public ResponseEntity<DeliveryResponseDTO> getDeliveryByParcelNumber(@PathVariable String parcelNumber) {
        DeliveryResponseDTO delivery = deliveryService.getDeliveryByParcelNumber(parcelNumber);
        return ResponseEntity.ok(delivery);
    }


    // Endpoint pour récupérer les livraisons où l'utilisateur est sender ou receiver
    @GetMapping("/user")
    public ResponseEntity<List<DeliveryResponseDTO>> getUserDeliveries() {
        List<DeliveryResponseDTO> deliveries = deliveryService.getDeliveriesForUser();
        return ResponseEntity.ok(deliveries);
    }


    // Endpoint pour récupérer les livraisons associées aux trajets publiés par l'utilisateur
    @GetMapping("/user/published-trips")
    public ResponseEntity<List<DeliveryResponseDTO>> getUserPublishedTripDeliveries() {
        List<DeliveryResponseDTO> deliveries = deliveryService.getDeliveriesForPublishedTrips();
        return ResponseEntity.ok(deliveries);
    }





    @PostMapping
    public ResponseEntity<DeliveryResponseDTO> createDelivery(@Valid @RequestBody DeliveryRequestDTO requestDTO) {
        DeliveryResponseDTO responseDTO = deliveryService.createDelivery(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

     //======================================== Methode pour creer un Delivery avec un parcel qui existe ======================
    @PostMapping("existing_parcel")
    public ResponseEntity<DeliveryResponseDTO> createDelivery_with_existing_parcel(@Valid @RequestBody DeliveryRequest_with_existing_parcelDTO requestDTO ){
        DeliveryResponseDTO responseDTO = deliveryService.createDelivery_with_existing_parcel(requestDTO);
        return ResponseEntity.ok(responseDTO);

    }
    //========================================================================================================================





    @PatchMapping("/{deliveryNumber}/status")
            public ResponseEntity<DeliveryResponseDTO> patchDeliveryStatus(
                    @PathVariable String deliveryNumber,
                    @RequestBody DeliveryStatusUpdateDTO statusUpdateDTO) {
                        
                DeliveryResponseDTO responseDTO = deliveryService.patchDeliveryStatusByNumber(deliveryNumber, statusUpdateDTO.getStatus());
                
                return ResponseEntity.ok(responseDTO);
            }




    @GetMapping("/trip/{tripNumber}/parcels-request")
    public ResponseEntity<List<DeliveryParcelResponseDTO>> getParcelsByTripAndStatus(
            @PathVariable String tripNumber,
            @RequestParam(defaultValue = "REQUESTED") DeliveryStatus status) {

        List<DeliveryParcelResponseDTO> responseDTOs = deliveryService.getParcelsByTripAndStatus(tripNumber, status);

        if (responseDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(responseDTOs);
    }



    @GetMapping("/trip/{tripNumber}/parcels-trip")
    public ResponseEntity<List<DeliveryParcelResponseDTO>> getParcelsByTripExcludingStatuses(
            @PathVariable String tripNumber) {

        List<DeliveryStatus> excludedStatuses = List.of(DeliveryStatus.REQUESTED, DeliveryStatus.CANCELLED);
        List<DeliveryParcelResponseDTO> responseDTOs = deliveryService.getParcelsByTripExcludingStatuses(tripNumber, excludedStatuses);

        if (responseDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(responseDTOs);
    }


 
    

}
