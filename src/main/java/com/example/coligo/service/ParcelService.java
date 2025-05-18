package com.example.coligo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.coligo.dto.request.ParcelRequestDTO;
import com.example.coligo.dto.response.ParcelResponseDTO;
import com.example.coligo.enums.ParcelStatusType;
import com.example.coligo.mapper.ParcelMapper;
import com.example.coligo.model.Parcel;
import com.example.coligo.model.User;
import com.example.coligo.repository.ParcelRepository;
import com.example.coligo.util.HashUtil;

import jakarta.transaction.Transactional;



@Service
public class ParcelService {

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ParcelMapper parcelMapper;


    public List<ParcelResponseDTO> getAllParcels() {
        List<Parcel> parcels = parcelRepository.findAll();
        return parcels.stream()
                .map(parcelMapper::toResponseDTO)
                .collect(Collectors.toList());
    }



    public Parcel getParcel(String parcelNumber) {
        Parcel parcel = parcelRepository.findByParcelNumber(parcelNumber).orElseThrow(() -> new RuntimeException("Parcel not found"));
        if (parcel == null) {
            throw new RuntimeException("Parcel not found with parcel number: " + parcelNumber);
        }
        return parcel;
    }


    public Parcel getParcel(Long id) {
        Parcel parcel = parcelRepository.findById(id).orElseThrow(() -> new RuntimeException("Parcel not found"));
        return parcel;
    }



    public List<ParcelResponseDTO> getParcelsForCurrentUser() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        List<Parcel> parcels = parcelRepository.findBySenderOrReceiver(currentUser, currentUser);
        return parcels.stream()
                .map(parcelMapper::toResponseDTO)
                .collect(Collectors.toList());
    }


    public Map<String, List<ParcelResponseDTO>> getParcelsForCurrentUser_2() {
        User currentUser = userService.getCurrentAuthenticatedUser();

        // Récupérer les parcels où l'utilisateur courant est soit sender soit receiver
        List<Parcel> parcels = parcelRepository.findBySenderOrReceiver(currentUser, currentUser);

        // Filtrer et transformer les parcels où l'utilisateur est le sender
        List<ParcelResponseDTO> sentParcels = parcels.stream()
                .filter(parcel -> parcel.getSender().equals(currentUser))
                .map(parcelMapper::toResponseDTO)
                .collect(Collectors.toList());

        // Filtrer et transformer les parcels où l'utilisateur est le receiver
        List<ParcelResponseDTO> receivedParcels = parcels.stream()
                .filter(parcel -> parcel.getReceiver().equals(currentUser))
                .map(parcelMapper::toResponseDTO)
                .collect(Collectors.toList());

        // Retourner les deux listes dans une Map pour les différencier
        Map<String, List<ParcelResponseDTO>> result = new HashMap<>();
        result.put("sent", sentParcels);
        result.put("received", receivedParcels);

        return result;
    }



   
    public ParcelResponseDTO getParcelByParcelNumber(String parcelNumber) {
        Parcel parcel = getParcel(parcelNumber);
        return parcelMapper.toResponseDTO(parcel);
    }





    
    // @Transactional
    // public ParcelResponseDTO createParcel(ParcelRequestDTO requestDTO) {
    //     User sender = userService.getCurrentAuthenticatedUser();
    //     User receiver = userService.getUserByEmail(requestDTO.getReceiverEmail());
    //     Parcel parcel = parcelMapper.toEntity(requestDTO, sender, receiver);
    //     Parcel savedParcel = parcelRepository.save(parcel);
    //     return parcelMapper.toResponseDTO(savedParcel);
    // }

    @Transactional
    public Parcel createParcel(ParcelRequestDTO requestDTO) {
        User sender = userService.getCurrentAuthenticatedUser();
        User receiver = userService.getUserByEmail(requestDTO.getReceiverEmail());
        Parcel parcel = parcelMapper.toEntity(requestDTO, sender, receiver);

        // Génération de rawData unique pour chaque token
        String rawDataForDelivery = parcel.getParcelNumber() + ":DELIVERY:" + LocalDateTime.now();
        String rawDataForPickup = parcel.getParcelNumber() + ":PICKUP:" + LocalDateTime.now();

        // Création des tokens avec des entrées distinctes
        parcel.setParcelNumber(generateParcelCode());
        parcel.setDeliveryToken(HashUtil.generateSecureHash(rawDataForDelivery));
        parcel.setPickupToken(HashUtil.generateSecureHash(rawDataForPickup));

        Parcel savedParcel = parcelRepository.save(parcel);
        return savedParcel;
    }


    private String generateParcelCode() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomPart = (int) (Math.random() * 9000) + 1000;
        return "SPN" + datePart + randomPart;
    }

//=========================================================================================
    @Transactional
    public ParcelResponseDTO createParcelAlone(ParcelRequestDTO requestDTO ) {
        System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
        User sender = userService.getCurrentAuthenticatedUser( );
        User receiver = userService.getUserByEmail(requestDTO.getReceiverEmail());
        Parcel parcel = parcelMapper.toEntity(requestDTO, sender, receiver);
        parcel.setCurrentStatus(ParcelStatusType.NO_TRIP); 
        //parcel.addStatusToHistory(ParcelStatusType.NO_TRIP); //ce truc bug parce que l'historique est null

        // Génération de rawData unique pour chaque token
        String rawDataForDelivery = parcel.getParcelNumber() + ":DELIVERY:" + LocalDateTime.now();
        String rawDataForPickup = parcel.getParcelNumber() + ":PICKUP:" + LocalDateTime.now();

        parcel.setParcelNumber(generateParcelCode());
        //ici pas besoin de token

        // Création des tokens avec des entrées distinctes
        parcel.setDeliveryToken(HashUtil.generateSecureHash(rawDataForDelivery));
        parcel.setPickupToken(HashUtil.generateSecureHash(rawDataForPickup));

        System.out.println("FIN BBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
         //parcel.afficherParcel(parcel);
        Parcel savedParcel = parcelRepository.save(parcel);
        System.out.println("FIN STACKOVERFLOW ? ");
        
        return parcelMapper.toResponseDTO(savedParcel); //.onCreate() va etre appelé a peu pres la 
        
    }
    //============================================================================================


    

    public ParcelResponseDTO updateParcelStatus(String parcelNumber, ParcelStatusType newStatus) {
        Parcel parcel = getParcel(parcelNumber);
        if (!isValidStatusTransition(parcel.getCurrentStatus(), newStatus)) {
            throw new IllegalArgumentException(
                "Invalid status transition from " + parcel.getCurrentStatus() + " to " + newStatus
            );
        }
        
        parcel.setCurrentStatus(newStatus);
        Parcel updatedParcel = parcelRepository.save(parcel);
        return parcelMapper.toResponseDTO(updatedParcel);
    }


    public Parcel updateParcelStatus(Parcel parcel, ParcelStatusType newStatus) {
        
        if (!isValidStatusTransition(parcel.getCurrentStatus(), newStatus)) {
            throw new IllegalArgumentException(
                "Invalid status transition from " + parcel.getCurrentStatus() + " to " + newStatus
            );
        }
        
        parcel.setCurrentStatus(newStatus);
        Parcel updatedParcel = parcelRepository.save(parcel);
        return updatedParcel;
    }
    



    private boolean isValidStatusTransition(ParcelStatusType currentStatus, ParcelStatusType newStatus) {
        Map<ParcelStatusType, List<ParcelStatusType>> allowedTransitions = Map.of(
            ParcelStatusType.CREATED, List.of(ParcelStatusType.WAIT_PICK_UP, ParcelStatusType.CANCELLED),
            ParcelStatusType.WAIT_PICK_UP, List.of(ParcelStatusType.IN_TRANSIT, ParcelStatusType.CANCELLED),
            ParcelStatusType.IN_TRANSIT, List.of(ParcelStatusType.DELIVERED, ParcelStatusType.CANCELLED),
            ParcelStatusType.DELIVERED, List.of(), // Aucun statut possible après DELIVERED
            ParcelStatusType.CANCELLED, List.of() // Aucun statut possible après CANCELLED
        );
        return allowedTransitions.getOrDefault(currentStatus, List.of()).contains(newStatus);
    }



    public List<Parcel> getParcelsBySenderWithNoTrip(User sender) {
        return parcelRepository.findBySenderAndCurrentStatus(sender, ParcelStatusType.NO_TRIP);
    }
    

    public List<Parcel> getParcelsWithNoTrip() {
        return parcelRepository.findByCurrentStatus(ParcelStatusType.NO_TRIP);
    }
   
}
