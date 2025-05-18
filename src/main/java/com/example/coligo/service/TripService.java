package com.example.coligo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import com.example.coligo.dto.request.TripRequestDTO;
import com.example.coligo.dto.request.TripStatusUpdateDTO;
import com.example.coligo.dto.response.TripResponseDTO;
import com.example.coligo.enums.ParcelStatusType;
import com.example.coligo.enums.TripStatus;
import com.example.coligo.mapper.TripMapper;
import com.example.coligo.model.Location;
import com.example.coligo.model.Parcel;
import com.example.coligo.model.Trip;
import com.example.coligo.model.User;
import com.example.coligo.repository.TripRepository;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;




@Service
public class TripService {
    
    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private LocationService locationService;

    @Autowired
    private GoogleMapService googleMapService;

    @Autowired
    private ParcelService parcelService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private TripMapper tripMapper;




    public List<TripResponseDTO> getAllTrips() {
        List<Trip> trips = tripRepository.findAll();
        return trips.stream()
                    .map(tripMapper::toResponseDTO)
                    .collect(Collectors.toList());
    }


    public TripResponseDTO getTripById(Long id) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found"));
        return tripMapper.toResponseDTO(trip);
    }



    public Trip _getTripByNumber(String tripNumber) {
        return tripRepository.findByTripNumber(tripNumber).orElseThrow(() -> new RuntimeException("Trip not found with number: " + tripNumber));
    }



    public TripResponseDTO getTripByNumber(String tripNumber) {
        Trip trip = _getTripByNumber(tripNumber);
        return tripMapper.toResponseDTO(trip);
    }
    


    public List<TripResponseDTO> getTripsForCurrentAuthenticatedUser() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        List<Trip> trips = tripRepository.findByPublisher(currentUser);
        return trips.stream()
                    .map(tripMapper::toResponseDTO)
                    .toList();
    }

    
    @Transactional
    public TripResponseDTO createTrip(TripRequestDTO dto) {
        User publisher = userService.getCurrentAuthenticatedUser();
        Location startLocation = locationService.getOrCreateLocation(dto.getStartLocation());
        Location endLocation = locationService.getOrCreateLocation(dto.getEndLocation());
        Trip trip = tripMapper.toEntity(dto, startLocation, endLocation, publisher);

        trip.setStatus(TripStatus.OPEN);
        trip.setTripNumber(generateTripCode());
        Trip savedTrip = tripRepository.save(trip);
        notifyInterestedSenders(savedTrip);
        return tripMapper.toResponseDTO(savedTrip);
    }

    private String generateTripCode() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomPart = (int) (Math.random() * 9000) + 1000;
        return "STN" + datePart + randomPart;
    }




    public TripResponseDTO updateTrip(String tripNumber, TripRequestDTO tripRequestDTO) {
      
        Trip trip = _getTripByNumber(tripNumber);

        Location startLocation = locationService.getOrCreateLocation(tripRequestDTO.getStartLocation());
        Location endLocation = locationService.getOrCreateLocation(tripRequestDTO.getEndLocation());

        trip.setStartLocation(startLocation);
        trip.setEndLocation(endLocation);
        trip.setDepartureTime(tripRequestDTO.getDepartureTime());
        trip.setDistance(tripRequestDTO.getDistance());
        trip.setDuration(tripRequestDTO.getDuration());
        trip.setPolyline(tripRequestDTO.getPolyline());

        Trip updatedTrip = tripRepository.save(trip);
        return tripMapper.toResponseDTO(updatedTrip);
    }
    



    public List<TripResponseDTO> searchTrips(Double startLatitude, Double startLongitude,Double endLatitude, Double endLongitude, String departureDate) {
        Specification<Trip> spec = Specification.where(null);

        // Critère : Localisation de départ
        if (startLatitude != null && startLongitude != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("startLocation").get("latitude"), startLatitude),
                    criteriaBuilder.equal(root.get("startLocation").get("longitude"), startLongitude)
            ));
        }

        // Critère : Localisation d'arrivée
        if (endLatitude != null && endLongitude != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("endLocation").get("latitude"), endLatitude),
                    criteriaBuilder.equal(root.get("endLocation").get("longitude"), endLongitude)
            ));
        }

        // Critère : Date de départ
        if (departureDate != null && !departureDate.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                LocalDateTime dateStart = LocalDateTime.parse(departureDate + "T00:00:00");
                LocalDateTime dateEnd = LocalDateTime.parse(departureDate + "T23:59:59");
                return criteriaBuilder.between(root.get("departureTime"), dateStart, dateEnd);
            });
        }

        // Exécutez la recherche
        List<Trip> trips = tripRepository.findAll(spec);
        return trips.stream()
                .map(tripMapper::toResponseDTO)
                .toList();
    }







    public List<TripResponseDTO> searchTrips_2(Double startLatitude, Double startLongitude,Double endLatitude, Double endLongitude, String departureDate) {
        LocalDate date = LocalDate.parse(departureDate);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        // Récupérer tous les trajets ouverts
        List<Trip> openTrips = tripRepository.findByStatus(TripStatus.OPEN);

        // Filtrer par proximité des coordonnées de départ et d'arrivée
        double radius = 50.0; // Rayon de recherche en kilomètres (modifiable)
        openTrips = openTrips.stream()
                .filter(trip -> locationService.calculateDistance(
                        trip.getStartLocation().getLatitude(), trip.getStartLocation().getLongitude(),
                        startLatitude, startLongitude) <= radius)
                .filter(trip -> locationService.calculateDistance(
                        trip.getEndLocation().getLatitude(), trip.getEndLocation().getLongitude(),
                        endLatitude, endLongitude) <= radius)
                .filter(trip -> trip.getDepartureTime().isAfter(startOfDay) &&
                        trip.getDepartureTime().isBefore(endOfDay))
                .collect(Collectors.toList());

        // Convertir les trajets en DTOs pour la réponse
        return openTrips.stream()
                        .map(tripMapper::toResponseDTO)
                        .collect(Collectors.toList());
    }



    public List<TripResponseDTO> searchTrips(String pickupAddress, String deliveryAddress, LocalDate date) {

        // Récupérer les coordonnées de départ et d'arrivée
        double[] startCoordinates = googleMapService.getCoordinates(pickupAddress);
        double[] endCoordinates = googleMapService.getCoordinates(deliveryAddress);
    
        Double startLatitude = startCoordinates[0];
        Double startLongitude = startCoordinates[1];
        Double endLatitude = endCoordinates[0];
        Double endLongitude = endCoordinates[1];
    
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
    
        // Récupérer tous les trajets ouverts
        List<Trip> openTrips = tripRepository.findByStatus(TripStatus.OPEN);
    
        // Filtrer par proximité des coordonnées de départ et d'arrivée
        double radius = 50.0; // Rayon de recherche en kilomètres (modifiable)
        openTrips = openTrips.stream()
                .filter(trip -> locationService.calculateDistance(
                        trip.getStartLocation().getLatitude(), trip.getStartLocation().getLongitude(),
                        startLatitude, startLongitude) <= radius)
                .filter(trip -> locationService.calculateDistance(
                        trip.getEndLocation().getLatitude(), trip.getEndLocation().getLongitude(),
                        endLatitude, endLongitude) <= radius)
                .filter(trip -> trip.getDepartureTime().isAfter(startOfDay) &&
                        trip.getDepartureTime().isBefore(endOfDay))
                .collect(Collectors.toList());
    
        // Convertir les trajets en DTOs pour la réponse
        return openTrips.stream()
                        .map(tripMapper::toResponseDTO)
                        .collect(Collectors.toList());
    }
    


    public List<TripResponseDTO> searchTrips_v3(String pickupAddress, String deliveryAddress, LocalDate dateLimit) {

        double[] startCoordinates = googleMapService.getCoordinates(pickupAddress);
        double[] endCoordinates = googleMapService.getCoordinates(deliveryAddress);
    
        Double startLatitude = startCoordinates[0];
        Double startLongitude = startCoordinates[1];
        Double endLatitude = endCoordinates[0];
        Double endLongitude = endCoordinates[1];
    
        // Date actuelle
        LocalDate today = LocalDate.now();
    
        // Vérification de la logique temporelle
        if (today.isAfter(dateLimit)) {
            throw new IllegalArgumentException("La date limite doit être supérieure ou égale à la date actuelle.");
        }
    
        // Récupérer tous les trajets ouverts
        List<Trip> openTrips = tripRepository.findByStatus(TripStatus.OPEN);
    
        // Rayon de recherche en kilomètres (modifiable)
        double radius = 10.0; //ctrl_F_rayon
    
        // Filtrer les trajets
        openTrips = openTrips.stream()
                .filter(trip -> locationService.calculateDistance(
                        trip.getStartLocation().getLatitude(), trip.getStartLocation().getLongitude(),
                        startLatitude, startLongitude) <= radius)
                .filter(trip -> locationService.calculateDistance(
                        trip.getEndLocation().getLatitude(), trip.getEndLocation().getLongitude(),
                        endLatitude, endLongitude) <= radius)
                .filter(trip -> !trip.getDepartureTime().toLocalDate().isAfter(dateLimit)) // Vérifie que le trajet est avant ou égal à la date limite
                .filter(trip -> !trip.getDepartureTime().toLocalDate().isBefore(today)) // Vérifie que le trajet n'est pas dans le passé
                .collect(Collectors.toList());
    
        // Convertir les trajets en DTOs pour la réponse
        return openTrips.stream()
                .map(tripMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    


    public void deleteTrip(String tripNumber) {
        Trip trip = _getTripByNumber(tripNumber);
    
        Location startLocation = trip.getStartLocation();
        Location endLocation = trip.getEndLocation();
        tripRepository.delete(trip);
    
        // Vérifier si les locations sont utilisées par d'autres trips et les supprimer si elles ne le sont pas
        if (tripRepository.countByStartLocation(startLocation) == 0 &&
            tripRepository.countByEndLocation(startLocation) == 0) {
            locationService.deleteLocation(startLocation.getId());
        }
    
        if (tripRepository.countByStartLocation(endLocation) == 0 &&
            tripRepository.countByEndLocation(endLocation) == 0) {
            locationService.deleteLocation(endLocation.getId());
        }
    }




    public TripResponseDTO updateTripStatus(String tripNumber, TripStatusUpdateDTO statusUpdateDTO) {
        Trip trip = _getTripByNumber(tripNumber);
        User currentUser = userService.getCurrentAuthenticatedUser();

        // Vérifier les autorisations (l'utilisateur doit être le propriétaire du trip)
        if (!trip.getPublisher().equals(currentUser)) {
            throw new AccessDeniedException("You are not authorized to update this trip");
        }

        // Validation des transitions de statut
        if (!isValidStatusTransition(trip.getStatus(), statusUpdateDTO.getNewStatus())) {
            throw new IllegalArgumentException("Invalid status transition from " + trip.getStatus() + " to " + statusUpdateDTO.getNewStatus());
        }

        trip.setStatus(statusUpdateDTO.getNewStatus());
        tripRepository.save(trip);
        return tripMapper.toResponseDTO(trip);
    }



    private boolean isValidStatusTransition(TripStatus currentStatus, TripStatus newStatus) {

        Map<TripStatus, List<TripStatus>> allowedTransitions = Map.of(
                TripStatus.OPEN, List.of(TripStatus.IN_PROGRESS, TripStatus.CANCELLED),
                TripStatus.IN_PROGRESS, List.of(TripStatus.COMPLETED, TripStatus.CANCELLED),
                TripStatus.COMPLETED, List.of(),
                TripStatus.CANCELLED, List.of()
        );

        return allowedTransitions.getOrDefault(currentStatus, List.of()).contains(newStatus);
    }

       //============================= service pour recuperer tout les Trip dans un rayon ===========================
    //                               et avant date d'expiration 

    public List<TripResponseDTO> getTrips_nearby_start_and_end_before_expiracy(double radius, LocalDateTime expiracyDate, double longitudeDebut, double latitudeDebut,double longitudeFin, double latitudeFin){
            return tripRepository.findNearbyStartFinishTripsBeforeExpiracy(latitudeDebut,longitudeDebut,latitudeFin,longitudeFin,radius,expiracyDate)
                                            .stream()
                                            .map( tripMapper::toResponseDTO)
                                            .collect(Collectors.toList());
    }
    
    
    public List<TripResponseDTO> searchTrips_nearby_by_expiracy(String senderAddress, String recipientAddress, LocalDateTime expiracyDate) {

        // Récupérer les coordonnées des adresses
        double[] senderCoordinates = googleMapService.getCoordinates(senderAddress);
        double[] recipientCoordinates = googleMapService.getCoordinates(recipientAddress);
    
        // Rayon de recherche en kilomètres
        double radius = 50.0;
    
        // LocalDateTime startOfDay = date.atStartOfDay();
        // LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
    
        // Récupérer tous les trajets ouverts
        List<Trip> openTrips = tripRepository.findByStatus(TripStatus.OPEN);
    
        // Filtrer les trajets restants
        openTrips = openTrips.stream()
                // Vérifier la distance entre l'adresse du sender et la localisation de départ du trajet
                .filter(trip -> locationService.calculateDistance(
                        trip.getStartLocation().getLatitude(), trip.getStartLocation().getLongitude(),
                        senderCoordinates[0], senderCoordinates[1]) <= radius)
                // Vérifier la distance entre l'adresse du recipient et la localisation d'arrivée du trajet
                .filter(trip -> locationService.calculateDistance(
                        trip.getEndLocation().getLatitude(), trip.getEndLocation().getLongitude(),
                        recipientCoordinates[0], recipientCoordinates[1]) <= radius)
                // Filtrer les trajets par plage de dates
                // .filter(trip -> trip.getDepartureTime().isAfter(startOfDay) &&
                //         trip.getDepartureTime().isBefore(endOfDay))
                // Vérifier que le trajet n'a pas expiré
                .filter(trip -> trip.getDepartureTime().isBefore(expiracyDate))
                .collect(Collectors.toList());
    
        // Convertir les trajets en DTOs pour la réponse
        return openTrips.stream()
                .map(tripMapper::toResponseDTO)
                .collect(Collectors.toList());
    }





    

    @Async
    private void notifyInterestedSenders(Trip trip) {

        double radius = 50.0; // Rayon de recherche en kilomètres
        List<Parcel> parcelsWithNoTrip = parcelService.getParcelsWithNoTrip();

        // Parcourir les colis et envoyer les notifications aux expéditeurs intéressés
        for (Parcel parcel : parcelsWithNoTrip) {
            // Récupérer les localisations des adresses du colis
            Location senderLocation = googleMapService.getLocationFromAddress(parcel.getSenderAddress());
            Location recipientLocation = googleMapService.getLocationFromAddress(parcel.getRecipientAddress());

            // Vérifier si le colis est proche du trajet
            if (locationService.calculateDistance(
                    senderLocation.getLatitude(), senderLocation.getLongitude(),
                    trip.getStartLocation().getLatitude(), trip.getStartLocation().getLongitude()) <= radius &&
                locationService.calculateDistance(
                    recipientLocation.getLatitude(), recipientLocation.getLongitude(),
                    trip.getEndLocation().getLatitude(), trip.getEndLocation().getLongitude()) <= radius) {
                
                // User sender = parcel.getSender(); // Récupérer l'expéditeur
                // String subject = "Un trajet pourrait correspondre à votre colis";
                // String body = String.format(
                //     "Bonjour %s,\n\nUn nouveau trajet partant de %s vers %s pourrait correspondre à votre colis :\n\n"
                //     + "Description du colis : %s\n"
                //     + "Trajet : Départ %s, Destination %s\n"
                //     + "Date de départ : %s\n"
                //     + "Durée estimée : %d minutes\n\n"
                //     + "Merci de vérifier si ce trajet vous convient.\n\nCordialement,\nL'équipe de livraison.",
                //     sender.getFirstName(),
                //     parcel.getSenderAddress(),
                //     parcel.getRecipientAddress(),
                //     parcel.getDescription(),
                //     trip.getStartLocation().getAddress(),
                //     trip.getEndLocation().getAddress(),
                //     trip.getDepartureTime(),
                //     trip.getDuration()
                // );

                // // Envoyer l'email à l'expéditeur
                // emailService.sendEmail(sender.getEmail(), subject, body);


                int durationInMinutes = trip.getDuration(); // Exemple : 145 minutes

                // Calcul des heures et des minutes
                int hours = durationInMinutes / 60;
                int minutes = durationInMinutes % 60;

                // Formatez la durée
                String formattedDuration = hours + " heures et " + minutes + " minutes";



                 // Ajoutez des dates correctement typées
                LocalDateTime departureDate = trip.getDepartureTime();
                LocalDateTime arrivalDate = departureDate.plusMinutes(trip.getDuration());

                // Préparer les variables dynamiques pour l'email
                Context context = new Context();
                context.setLocale(Locale.FRANCE);
                context.setVariable("senderName", parcel.getSender().getFirstName());
                context.setVariable("departureLocation", trip.getStartLocation().getAddress());
                context.setVariable("arrivalLocation", trip.getEndLocation().getAddress());
                context.setVariable("departureDate", departureDate);
                context.setVariable("arrivalDate", arrivalDate);
                context.setVariable("duration", formattedDuration);
                context.setVariable("distance", trip.getDistance());

                // Envoyer l'email
        
                emailService.sendHtmlEmailWithTemplate(
                    parcel.getSender().getEmail(),
                    "Nouveau trajet disponible pour votre colis",
                    "trip-email-template", // Nom du fichier template (sans extension)
                    context
                );
            


                
            }
        }
    }



}













