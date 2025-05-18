package com.example.coligo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.coligo.dto.request.ParcelRequestDTO;
import com.example.coligo.dto.response.ParcelResponseDTO;
import com.example.coligo.enums.ParcelStatusType;
import com.example.coligo.service.ParcelService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;





@RestController
@RequestMapping("/parcels")
public class ParcelController {

    @Autowired
    private ParcelService parcelService;

    @GetMapping
    public ResponseEntity<List<ParcelResponseDTO>> getAllParcels() {
        List<ParcelResponseDTO> parcels = parcelService.getAllParcels();
        return ResponseEntity.ok(parcels);
    }




    @GetMapping("/{parcelNumber}")
    public ResponseEntity<ParcelResponseDTO> getParcelByNumber(@PathVariable String parcelNumber) {
        ParcelResponseDTO parcel = parcelService.getParcelByParcelNumber(parcelNumber);
        return ResponseEntity.ok(parcel);
    }


    @GetMapping("/current-user-parcels")
    public ResponseEntity<Map<String, List<ParcelResponseDTO>>> getParcelsForCurrentUser() {
        Map<String, List<ParcelResponseDTO>> parcels = parcelService.getParcelsForCurrentUser_2();
        return ResponseEntity.ok(parcels);
    }
   

    @PatchMapping("/{parcelNumber}/status")
    public ResponseEntity<ParcelResponseDTO> updateParcelStatus(@PathVariable String parcelNumber, @RequestParam ParcelStatusType newStatus) {
        ParcelResponseDTO updatedParcel = parcelService.updateParcelStatus(parcelNumber, newStatus);
        return ResponseEntity.ok(updatedParcel);
    }

    @PostMapping
    public ResponseEntity<ParcelResponseDTO> createParcel(@Valid @RequestBody ParcelRequestDTO requestDTO) {
        System.out.println("DEBUT POST ");
        ParcelResponseDTO responseDTO = parcelService.createParcelAlone(requestDTO);
        System.out.println("FIN POST");
        return ResponseEntity.ok(responseDTO);
    }


}
