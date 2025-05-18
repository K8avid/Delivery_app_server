package com.example.coligo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.coligo.model.Notification;
import com.example.coligo.model.Parcel;
import com.example.coligo.model.Trip;
import com.example.coligo.model.User;
import com.example.coligo.repository.NotificationRepository;
import com.example.coligo.repository.ParcelRepository;
import com.example.coligo.repository.TripRepository;
import com.example.coligo.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") // Seuls les admins peuvent accéder
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    // Statistiques Générales
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalParcels", parcelRepository.count());
        stats.put("totalTrips", tripRepository.count());
        stats.put("unreadNotifications", notificationRepository.countByIsReadFalse());

        return ResponseEntity.ok(stats);
    }

    // Gestion des Utilisateurs
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // Modifier un utilisateur spécifique
    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Mise à jour de l'utilisateur
        user.setEmail(updatedUser.getEmail());
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setVehicle(updatedUser.getVehicle());
        user.setAddress(updatedUser.getAddress());
        user.setCity(updatedUser.getCity());
        user.setCountry(updatedUser.getCountry());

        userRepository.save(user);
        return ResponseEntity.ok("Utilisateur mis à jour avec succès.");
    }

    // Supprimer un utilisateur
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("Utilisateur supprimé.");
    }

    // Gestion des Colis
    @GetMapping("/parcels")
    public ResponseEntity<List<Parcel>> getAllParcels() {
        return ResponseEntity.ok(parcelRepository.findAll());
    }

    // Afficher tous les colis d'un utilisateur
    @GetMapping("/users/{userId}/parcels")
    public ResponseEntity<List<Parcel>> getParcelsByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        List<Parcel> parcels = parcelRepository.findBySenderOrReceiver(user, user);
        return ResponseEntity.ok(parcels);
    }

    @PutMapping("/users/{userId}/parcels/{id}")
    public ResponseEntity<String> updateParcel(@PathVariable Long userId, @PathVariable Long id, @RequestBody Parcel updatedParcel) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Parcel parcel = parcelRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Colis non trouvé"));

        // Vérifier si l'utilisateur est admin OU s'il est le sender/receiver du colis
        if (!user.getRole().equals("ADMIN") && !parcel.getSender().getId().equals(userId) && !parcel.getReceiver().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'avez pas le droit de modifier ce colis.");
        }

        // Mise à jour du colis
        parcel.setSenderAddress(updatedParcel.getSenderAddress());
        parcel.setRecipientAddress(updatedParcel.getRecipientAddress());
        parcel.setCurrentStatus(updatedParcel.getCurrentStatus());

        parcelRepository.save(parcel);
        return ResponseEntity.ok("Colis mis à jour avec succès.");
    }


    // Supprimer un colis spécifique associé à un utilisateur
    @DeleteMapping("/users/{userId}/parcels/{id}")
    public ResponseEntity<String> deleteParcel(@PathVariable Long userId, @PathVariable Long id) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Parcel parcel = parcelRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Colis non trouvé"));

        // Vérifier si l'utilisateur est admin OU s'il est le sender/receiver du colis
        if (!user.getRole().equals("ADMIN") && !parcel.getSender().getId().equals(userId) && !parcel.getReceiver().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'avez pas le droit de supprimer ce colis.");
        }

        // Supprimer le colis
        parcelRepository.delete(parcel);
        return ResponseEntity.ok("Colis supprimé avec succès.");
    }

    // Gestion des Trajets
    @GetMapping("/trips")
    public ResponseEntity<List<Trip>> getAllTrips() {
        return ResponseEntity.ok(tripRepository.findAll());
    }

    // Afficher tous les trajets d'un utilisateur
    @GetMapping("/users/{userId}/trips")
    public ResponseEntity<List<Trip>> getTripsByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        List<Trip> trips = tripRepository.findByPublisher(user);
        return ResponseEntity.ok(trips);
    }

   // Modifier un trajet spécifique associé à un utilisateur (Admin ou Publisher)
    @PutMapping("/users/{userId}/trips/{id}")
    public ResponseEntity<String> updateTrip(@PathVariable Long userId, @PathVariable Long id, @RequestBody Trip updatedTrip) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Trip trip = tripRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));

        // Vérifier si l'utilisateur est le publisher du trajet ou un administrateur
        if (!trip.getPublisher().getId().equals(userId) && !user.getRole().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'avez pas la permission de modifier ce trajet.");
        }

        // Mise à jour du trajet
        trip.setStartLocation(updatedTrip.getStartLocation());
        trip.setEndLocation(updatedTrip.getEndLocation());
        trip.setDepartureTime(updatedTrip.getDepartureTime());
        trip.setDistance(updatedTrip.getDistance());
        trip.setDuration(updatedTrip.getDuration());
        trip.setPolyline(updatedTrip.getPolyline());
        trip.setStatus(updatedTrip.getStatus());

        tripRepository.save(trip);
        return ResponseEntity.ok("Trajet mis à jour avec succès.");
    }

    // Supprimer un trajet spécifique associé à un utilisateur (Admin ou Publisher)
    @DeleteMapping("/users/{userId}/trips/{id}")
    public ResponseEntity<String> deleteTrip(@PathVariable Long userId, @PathVariable Long id) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Trip trip = tripRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));

        // Vérifier si l'utilisateur est le publisher du trajet ou un administrateur
        if (!trip.getPublisher().getId().equals(userId) && !user.getRole().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'avez pas la permission de supprimer ce trajet.");
        }

        // Supprimer le trajet
        tripRepository.delete(trip);
        return ResponseEntity.ok("Trajet supprimé avec succès.");
    }


    // Gestion des Notifications
    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<String> markNotificationAsRead(@PathVariable Long id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
        return ResponseEntity.ok("Notification marquée comme lue.");
    }
}

