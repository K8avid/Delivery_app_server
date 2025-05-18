package com.example.coligo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.coligo.dto.request.DeliveryRequestDTO;
import com.example.coligo.dto.request.DeliveryRequest_with_existing_parcelDTO;
import com.example.coligo.dto.request.QrTokenRequestDTO;
import com.example.coligo.dto.response.DeliveryParcelResponseDTO;
import com.example.coligo.dto.response.DeliveryResponseDTO;
import com.example.coligo.enums.DeliveryStatus;
import com.example.coligo.enums.ParcelStatusType;
import com.example.coligo.mapper.DeliveryMapper;
import com.example.coligo.model.Delivery;
import com.example.coligo.model.Parcel;
import com.example.coligo.model.Trip;
import com.example.coligo.model.User;
import com.example.coligo.repository.DeliveryRepository;
import com.example.coligo.repository.ParcelRepository;
import com.example.coligo.repository.TripRepository;
import com.example.coligo.util.StatusTransition;
import com.example.coligo.util.TransitionAction;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class DeliveryService {

    private ParcelRepository parcelRepository;

    private DeliveryRepository deliveryRepository;

    private NotificationService notificationService;

    private TripRepository tripRepository;

    private DeliveryMapper deliveryMapper;

    private UserService userService;

    private ParcelService parcelService;


    
    private final Map<StatusTransition, TransitionAction> transitionActions = new HashMap<>();



    public DeliveryService(
    ParcelRepository parcelRepository,
    DeliveryRepository deliveryRepository,
    NotificationService notificationService,
    TripRepository tripRepository,
    DeliveryMapper deliveryMapper,
    UserService userService,
    ParcelService parcelService
) {
    this.parcelRepository = parcelRepository;
    this.deliveryRepository = deliveryRepository;
    this.notificationService = notificationService;
    this.tripRepository = tripRepository;
    this.deliveryMapper = deliveryMapper;
    this.userService = userService;
    this.parcelService = parcelService;

    initializeTransitionActions();
}







    


















    // Récupérer toutes les livraisons
    public List<DeliveryResponseDTO> getAllDeliveries() {
        List<Delivery> deliveries = deliveryRepository.findAll();
        return deliveries.stream()
                .map(deliveryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }



     // Récupérer une livraison par son numéro
     public DeliveryResponseDTO getDeliveryByNumber(String deliveryNumber) {
        Delivery delivery = deliveryRepository.findByDeliveryNumber(deliveryNumber).orElseThrow(() -> new RuntimeException("Delivery not found with number: " + deliveryNumber));
        return deliveryMapper.toResponseDTO(delivery);
    }


    
    // Récupérer une livraison par son ID
    public DeliveryResponseDTO getDeliveryById(Long id) {
        Delivery delivery = deliveryRepository.findById(id).orElseThrow(() -> new RuntimeException("Delivery not found with ID: " + id));
        return deliveryMapper.toResponseDTO(delivery);
    }



    
    // Récupérer les livraisons où l'utilisateur est sender ou receiver
    public List<DeliveryResponseDTO> getDeliveriesForUser() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        List<Delivery> deliveries = deliveryRepository.findBySenderOrReceiver(currentUser);
        return deliveries.stream()
                .map(delivery -> deliveryMapper.toResponseDTO(delivery))
                .collect(Collectors.toList());
    }



    // Récupérer les livraisons associées aux trajets publiés par l'utilisateur
    public List<DeliveryResponseDTO> getDeliveriesForPublishedTrips() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        List<Delivery> deliveries = deliveryRepository.findByTripPublisher(currentUser);
        return deliveries.stream()
                .map(delivery -> deliveryMapper.toResponseDTO(delivery))
                .collect(Collectors.toList());
    }


    // Créer une nouvelle livraison
    public DeliveryResponseDTO createDelivery(DeliveryRequestDTO requestDTO) {
        Trip trip = tripRepository.findByTripNumber(requestDTO.getTripNumber()).orElseThrow(() -> new RuntimeException("Trip not found with trip number: " + requestDTO.getTripNumber()));
        Parcel savedParcel = parcelService.createParcel(requestDTO.getParcel());
        
        Delivery delivery = Delivery.builder()
                .trip(trip)
                .parcel(savedParcel)
                .status(DeliveryStatus.REQUESTED)
                .deliveryNumber(generateDeliveryNumber())
                .build();

        Delivery savedDelivery = deliveryRepository.save(delivery);


       

        return deliveryMapper.toResponseDTO(savedDelivery);
    }

        //=======================================================================================
    //Creer une nouvelle livraison avec un parcel existant 
    public DeliveryResponseDTO createDelivery_with_existing_parcel(DeliveryRequest_with_existing_parcelDTO requestDTO) {

        Trip trip = tripRepository.findByTripNumber(requestDTO.getTripNumber()).orElseThrow(() -> new RuntimeException("Trip not found with trip number: " + requestDTO.getTripNumber()));
        Parcel parcel = parcelRepository.findByParcelNumber(requestDTO.getParcelNumber()).orElseThrow(() -> new RuntimeException("issou Parcel not found with parcel number: " + requestDTO.getTripNumber()));
        parcel.setCurrentStatus(ParcelStatusType.CREATED);
        parcel.setExpiracyDate(LocalDateTime.of(1900, 1, 1, 0, 0, 0));
        
        // en gros pour eviter de mettre la expiracyDate a null, j'ai mis 1er janvier 1900 comme date a ignorer
        
        Delivery delivery = Delivery.builder()
                .trip(trip)
                .parcel(parcel)
                .status(DeliveryStatus.REQUESTED)
                .deliveryNumber(generateDeliveryNumber())
                .build();

        Delivery savedDelivery = deliveryRepository.save(delivery);


        // ici on envoie des emails et notification aus personnes concernés pour qu'ils acceptent ou refusent
        // ici on envoie des emails et notification aus personnes concernés pour qu'ils acceptent ou refusent
        notificationService.createNotification(
                savedDelivery.getTrip().getPublisher(),
                "Demande de livraison pour votre trajet",
                "Un client a sélectionné votre trajet (" + savedDelivery.getTrip().getTripNumber() + ") pour livrer un colis : " + savedDelivery.getParcel().getDescription()
        );

        return deliveryMapper.toResponseDTO(savedDelivery);
        
    }
    //=========================================================================================



   


    // public DeliveryResponseDTO patchDeliveryStatusByNumber(String deliveryNumber, DeliveryStatus newStatus) {
    //     Delivery delivery = deliveryRepository.findByDeliveryNumber(deliveryNumber).orElseThrow(() -> new RuntimeException("Delivery not found with number: " + deliveryNumber));
    //     DeliveryStatus oldStatus = delivery.getStatus();
    
    //     if (!isValidStatusTransition(oldStatus, newStatus)) {
    //         throw new IllegalArgumentException(
    //                 "Invalid status transition from " + oldStatus + " to " + newStatus);
    //     }
    
    //     delivery.setStatus(newStatus);
    //     Delivery updatedDelivery = deliveryRepository.save(delivery);
    //     return deliveryMapper.toResponseDTO(updatedDelivery);
    // }
    
 

    public DeliveryResponseDTO patchDeliveryStatusByNumber(String deliveryNumber, DeliveryStatus newStatus) {
        Delivery delivery = deliveryRepository.findByDeliveryNumber(deliveryNumber).orElseThrow(() -> new RuntimeException("Delivery not found with number: " + deliveryNumber));
        DeliveryStatus oldStatus = delivery.getStatus();

        if (!isValidStatusTransition(oldStatus, newStatus)) {
            throw new IllegalArgumentException("Invalid status transition from " + oldStatus + " to " + newStatus);
        }


        
        // Exécuter l'action associée à la transition
        StatusTransition transition = new StatusTransition(oldStatus, newStatus);
        TransitionAction action = transitionActions.get(transition);
        

        if (action != null) {
            
            action.execute(delivery, newStatus);
            

        } else {
            System.out.println("No action defined for this transition.");
        }

        // Mettre à jour le statut
       
        delivery.setStatus(newStatus);
        Delivery updatedDelivery = deliveryRepository.save(delivery);
        return deliveryMapper.toResponseDTO(updatedDelivery);
    }


    private boolean isValidStatusTransition(DeliveryStatus oldStatus, DeliveryStatus newStatus) {
        Map<DeliveryStatus, List<DeliveryStatus>> allowedTransitions = Map.of(
            DeliveryStatus.REQUESTED, List.of(DeliveryStatus.PENDING_START, DeliveryStatus.CANCELLED),
            DeliveryStatus.PENDING_START, List.of(DeliveryStatus.IN_PROGRESS, DeliveryStatus.CANCELLED),
            DeliveryStatus.IN_PROGRESS, List.of(DeliveryStatus.COMPLETED, DeliveryStatus.CANCELLED),
            DeliveryStatus.COMPLETED, List.of(), // Aucun changement possible après COMPLETED
            DeliveryStatus.CANCELLED, List.of() // Aucun changement possible après CANCELLED
        );
    
        return allowedTransitions.getOrDefault(oldStatus, List.of()).contains(newStatus);
    }
    










    
    // Génération d'un numéro unique pour la livraison
    private String generateDeliveryNumber() {
        return "DLV" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
            ((int) (Math.random() * 9000) + 1000);
    }
    


    // public DeliveryResponseDTO getDeliveryByParcelNumber(String parcelNumber) {
    //     return deliveryRepository.findByParcelParcelNumber(parcelNumber)
    //             .map(deliveryMapper::toResponseDTO)
    //             .orElseThrow(() -> new RuntimeException("Delivery not found for Parcel Number: " + parcelNumber));
    // }


    public DeliveryResponseDTO getDeliveryByParcelNumber(String parcelNumber) {
        User currentUser = userService.getCurrentAuthenticatedUser();

        // Trouver la livraison associée au numéro de colis
        Delivery delivery = deliveryRepository.findByParcelParcelNumber(parcelNumber)
                .orElseThrow(() -> new RuntimeException("Delivery not found for Parcel Number: " + parcelNumber));

        // Construire la réponse en ajoutant le token dynamique
        return deliveryMapper.toResponseDTO(delivery, currentUser);
    }



    // public List<Parcel> getParcelsByTripAndStatus(String tripNumber, DeliveryStatus status) {
    //     // Appelle le repository pour récupérer les parcels
    //     return deliveryRepository.findParcelsByTripNumberAndStatus(tripNumber, status);
    // }


    // public List<Parcel> getParcelsByTripExcludingStatuses(String tripNumber, List<DeliveryStatus> excludedStatuses) {
    //     // Appelle le repository avec les statuts exclus
    //     return deliveryRepository.findParcelsByTripNumberAndExcludedStatuses(tripNumber, excludedStatuses);
    // }



    
     public List<DeliveryParcelResponseDTO> getParcelsByTripAndStatus(String tripNumber, DeliveryStatus status) {
        List<Delivery> deliveries = deliveryRepository.findByTripNumberAndStatus(tripNumber, status);

        return deliveries.stream()
                .map(delivery -> {
                    Parcel parcel = delivery.getParcel();
                    User sender = parcel.getSender();
                    return new DeliveryParcelResponseDTO(
                            delivery.getDeliveryNumber(),
                            parcel,
                            sender.getFirstName(),
                            sender.getLastName(),
                            sender.getPhoneNumber(),
                            sender.getEmail(),
                            delivery.getStatus()
                    );
                })
                .collect(Collectors.toList());
    }




    public List<DeliveryParcelResponseDTO> getParcelsByTripExcludingStatuses(String tripNumber, List<DeliveryStatus> excludedStatuses) {
        List<Delivery> deliveries = deliveryRepository.findByTripNumberAndExcludedStatuses(tripNumber, excludedStatuses);

        return deliveries.stream()
                .map(delivery -> {
                    Parcel parcel = delivery.getParcel();
                    User sender = parcel.getSender();
                    return new DeliveryParcelResponseDTO(
                            delivery.getDeliveryNumber(),
                            parcel,
                            sender.getFirstName(),
                            sender.getLastName(),
                            sender.getPhoneNumber(),
                            sender.getEmail(),
                            delivery.getStatus()
                    );
                })
                .collect(Collectors.toList());
    }











    public boolean verifyAndPickupParcel(String pickupToken, String tripNumber) {
        // Obtenir l'utilisateur actuel
        User currentUser = userService.getCurrentAuthenticatedUser();

        // Affichage de debug
        System.out.println("Pickup Token: " + pickupToken);
        System.out.println("Trip Number: " + tripNumber);

        // Rechercher la livraison correspondante via le pickupToken du Parcel
        Delivery delivery = deliveryRepository.findByParcelPickupTokenAndTripNumber(pickupToken, tripNumber);

        if (delivery == null) {
            System.out.println("gg");
            return false; // Aucun colis correspondant
            
        }

        // Vérifier si le statut est "PENDING_START"
        if (!DeliveryStatus.PENDING_START.equals(delivery.getStatus())) {
            System.out.println("mm");
            throw new IllegalStateException("Le colis n'est pas prêt à être récupéré.");
        }

        // Vérifier si l'utilisateur a les droits sur le trajet
        if (!delivery.getTrip().getPublisher().getId().equals(currentUser.getId())) {
            System.out.println("ss");
            throw new SecurityException("L'utilisateur n'a pas les droits pour ce trajet.");
        }


        patchDeliveryStatusByNumber(delivery.getDeliveryNumber(),DeliveryStatus.IN_PROGRESS);
        // // Mettre à jour le statut de la livraison à "IN_PROGRESS"
        // delivery.setStatus(DeliveryStatus.IN_PROGRESS);

        // // Mettre à jour le statut du Parcel associé à "IN_TRANSIT"
        // Parcel parcel = delivery.getParcel();
        // parcel.setCurrentStatus(ParcelStatusType.IN_TRANSIT);

        // // Sauvegarder les modifications
        // deliveryRepository.save(delivery);

        return true;
    }




     public boolean verifyAndDeliverParcel(QrTokenRequestDTO qrTokenRequestDTO) {
        String deliveryToken = qrTokenRequestDTO.getQrToken();
        String tripNumber = qrTokenRequestDTO.getTripNumber();

        // Obtenir l'utilisateur actuel
        User currentUser = userService.getCurrentAuthenticatedUser();

        // Rechercher la livraison correspondante via le deliveryToken du Parcel
        Delivery delivery = deliveryRepository.findByParcelDeliveryTokenAndTripNumber(deliveryToken, tripNumber);

        if (delivery == null) {
            return false; // Aucun colis correspondant
        }

        // Vérifier si le statut est "IN_PROGRESS"
        if (!DeliveryStatus.IN_PROGRESS.equals(delivery.getStatus())) {
            throw new IllegalStateException("Le colis n'est pas prêt à être livré.");
        }

        // Vérifier si l'utilisateur a les droits sur le trajet
        if (!delivery.getTrip().getPublisher().getId().equals(currentUser.getId())) {
            throw new SecurityException("L'utilisateur n'a pas les droits pour ce trajet.");
        }


        patchDeliveryStatusByNumber(delivery.getDeliveryNumber(),DeliveryStatus.COMPLETED);
        // // Mettre à jour le statut de la livraison à "COMPLETED"
        // delivery.setStatus(DeliveryStatus.COMPLETED);

        // // Mettre à jour le statut du Parcel associé à "DELIVERED"
        // Parcel parcel = delivery.getParcel();
        // parcel.setCurrentStatus(ParcelStatusType.DELIVERED);

        // // Sauvegarder les modifications
        // deliveryRepository.save(delivery);

        return true;
    }








    private void initializeTransitionActions() {
        transitionActions.put(
            new StatusTransition(DeliveryStatus.REQUESTED, DeliveryStatus.PENDING_START), 
            this::onRequestedToPendingStart
        );

        transitionActions.put(
            new StatusTransition(DeliveryStatus.PENDING_START, DeliveryStatus.IN_PROGRESS), 
            this::onPendingStartToInProgress
        );

        transitionActions.put(
            new StatusTransition(DeliveryStatus.IN_PROGRESS, DeliveryStatus.COMPLETED), 
            this::onInProgressToCompleted
        );

        transitionActions.put(
            new StatusTransition(DeliveryStatus.PENDING_START, DeliveryStatus.CANCELLED), 
            this::onCancelPendingStart
        );
    }





 
    // Actions spécifiques avec notifications
    private void onRequestedToPendingStart(Delivery delivery, DeliveryStatus newStatus) {
       

        String title = String.format("Mise à jour de la livraison : Colis #%s", delivery.getParcel().getParcelNumber());

        String message = String.format(
            "la demande livraison pour le colis #%s a été acceptée par le livreur. ",
            delivery.getParcel().getParcelNumber()
        );
        System.out.println("1===============================1111111111111111: je suis ici");
        parcelService.updateParcelStatus(delivery.getParcel(),ParcelStatusType.WAIT_PICK_UP);
        System.out.println("================================1111111111111111: je suis ici");
        notificationService.createNotification(delivery.getParcel().getSender(), title, message); 
        notificationService.createNotification(delivery.getParcel().getReceiver(), title, message);
    }


    private void onPendingStartToInProgress(Delivery delivery, DeliveryStatus newStatus) {
        String title = String.format("Mise à jour de la livraison : Colis #%s", delivery.getParcel().getParcelNumber());

        String message = String.format(
            "Le colis #%s est maintenant en cours de livraison. "
            + "Le livreur est en route pour effectuer la livraison.",
            delivery.getParcel().getParcelNumber()
        );

        // Notification pour l'expéditeur
        parcelService.updateParcelStatus(delivery.getParcel(),ParcelStatusType.IN_TRANSIT);
        notificationService.createNotification(delivery.getParcel().getSender(), title, message);
        notificationService.createNotification(delivery.getParcel().getReceiver(), title, message);
    }




    private void onInProgressToCompleted(Delivery delivery, DeliveryStatus newStatus) {
        String title = String.format("Mise à jour de la livraison : Colis #%s", delivery.getParcel().getParcelNumber());

        String message = String.format(
            "Le colis #%s a été livré avec succès. Merci d'avoir utilisé notre service. ",
            delivery.getParcel().getParcelNumber()
        );
    
        // Notification pour l'expéditeur
        parcelService.updateParcelStatus(delivery.getParcel(),ParcelStatusType.DELIVERED);
        notificationService.createNotification(delivery.getParcel().getSender(), title, message);
        notificationService.createNotification(delivery.getParcel().getReceiver(), title, message);
    }



    private void onCancelPendingStart(Delivery delivery, DeliveryStatus newStatus) {
        String title = String.format("Mise à jour de la livraison : Colis #%s", delivery.getParcel().getParcelNumber());

        String message = String.format(
            "La demande de livraison pour le colis #%s a été annulée. ",
            delivery.getParcel().getParcelNumber()
        );
    
        // Notification pour l'expéditeur
        parcelService.updateParcelStatus(delivery.getParcel(),ParcelStatusType.CANCELLED);
        notificationService.createNotification(delivery.getParcel().getSender(), title, message);
        notificationService.createNotification(delivery.getParcel().getReceiver(), title, message);
    }




}





