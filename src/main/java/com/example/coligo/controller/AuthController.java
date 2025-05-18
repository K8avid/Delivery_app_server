package com.example.coligo.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.coligo.dto.request.AuthenticationRequest;
import com.example.coligo.dto.request.RegisterRequest;
import com.example.coligo.dto.request.UserUpdateRequest;
import com.example.coligo.dto.response.AuthenticationResponse;
import com.example.coligo.dto.response.UserResponseDTO;
import com.example.coligo.model.User;
import com.example.coligo.repository.UserRepository;
import com.example.coligo.service.AuthenticationService;
import com.example.coligo.service.EmailService;
import com.example.coligo.service.UserService;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/auth")
public class AuthController {

    // @Value("${app.domain.url}")
    // private String domainUrl;

    @Value("")
    private String domainUrl;

    @Autowired
    private UserService userService;
    @Autowired
    
    private AuthenticationService service;
    @Autowired

    private UserRepository userRepository;
    @Autowired

    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }



    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }



    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getAuthenticatedUser() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email);

            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            UserResponseDTO response = UserResponseDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .dateOfBirth(user.getDateOfBirth())
                .phoneNumber(user.getPhoneNumber())
                .vehicle(user.getVehicle())
                .address(user.getAddress())
                .city(user.getCity())
                .country(user.getCountry())
                .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        try {
            // Supprime l'authentification actuelle
            SecurityContextHolder.clearContext();

            // (Optionnel) Ajouter une logique ici pour gérer l'invalidation des tokens
            // Exemple : Ajouter le token dans une liste noire ou marquer le token comme expiré

            return ResponseEntity.ok("Déconnexion réussie.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la déconnexion.");
        }
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser() {
        try {
            // Récupérer l'email de l'utilisateur authentifié
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            
            // Chercher l'utilisateur par son email
            User user = userService.getUserByEmail(email);
            
            if (user == null) {
                return ResponseEntity.notFound().build(); // Si l'utilisateur n'existe pas
            }

            // Supprimer l'utilisateur
            userService.deleteUser(user.getId());

            // Retourner un message de succès
            return ResponseEntity.ok("Compte supprimé avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la suppression du compte.");
        }
    }


   @PreAuthorize("isAuthenticated()")
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@Valid @RequestBody UserUpdateRequest updateRequest) {
        try {
            // Récupérer l'email de l'utilisateur authentifié
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            // Chercher l'utilisateur par son email
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.status(404).body("Utilisateur introuvable.");
            }

            // Mettre à jour les informations de l'utilisateur
            if (updateRequest.getFirstName() != null) {
                user.setFirstName(updateRequest.getFirstName());
            }
            if (updateRequest.getLastName() != null) {
                user.setLastName(updateRequest.getLastName());
            }
            if(updateRequest.getEmail() != null){
                user.setEmail(updateRequest.getEmail());
            }
            if (updateRequest.getDateOfBirth() != null) {
                user.setDateOfBirth(updateRequest.getDateOfBirth());
            }
            if (updateRequest.getPhoneNumber() != null) {
                user.setPhoneNumber(updateRequest.getPhoneNumber());
            }
            if (updateRequest.getAddress() != null) {
                user.setAddress(updateRequest.getAddress());
            }
            if (updateRequest.getCity() != null) {
                user.setCity(updateRequest.getCity());
            }
            if (updateRequest.getCountry() != null) {
                user.setCountry(updateRequest.getCountry());
            }
            if (updateRequest.getVehicle() != null) {
                user.setVehicle(updateRequest.getVehicle());
            }

            // Sauvegarder les modifications
            userService.saveUser(user);

            return ResponseEntity.ok("Vos informations ont été mises à jour avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur interne lors de la mise à jour des informations.");
        }
    }


    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        var user = userRepository.findByEmailVerificationToken(token)
            .orElseThrow(() -> new RuntimeException("Token invalide ou expiré"));

        user.setEmailVerified(true);
        user.setAccountEnabled(true);
        user.setAccountLocked(false);
        user.setEmailVerificationToken(null); // Supprimer le token après vérification
        userRepository.save(user);

        return ResponseEntity.ok("Email vérifié avec succès ! Vous pouvez maintenant vous connecter.");
    }

//endpoint pour reenvoyer le mail de verification 
    // @PostMapping("/resend-verification")
    // public ResponseEntity<String> resendVerificationEmail(@RequestParam("email") String email) {
    //     var user = userRepository.findByEmail(email)
    //         .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

    //     if (user.isEmailVerified()) {
    //         return ResponseEntity.badRequest().body("Email déjà vérifié.");
    //     }

    //     sendVerificationEmail(user);

    //     return ResponseEntity.ok("Email de vérification renvoyé avec succès.");
    // }

    // //endpoint pour reinitialiser le mot de passe
    // @PostMapping("/forgot-password")
    // public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
    //     String email = request.get("email");
    //     if (email == null || email.isEmpty()) {
    //         throw new IllegalArgumentException("L'email est requis.");
    //     }

    //     // Rechercher l'utilisateur dans la base de données
    //     var user = userRepository.findByEmail(email)
    //             .orElseThrow(() -> new RuntimeException("Email non trouvé dans la base de données."));

    //     // Générer un token unique pour la réinitialisation
    //     String resetToken = UUID.randomUUID().toString();
    //     user.setResetPasswordToken(resetToken);
    //     userRepository.save(user);

    //     // Construire le lien de réinitialisation
    //     String resetLink = "http://localhost:8080/reset-password?token=" + resetToken;
    //     String emailBody = "Bonjour,\n\n" +
    //             "Cliquez sur le lien suivant pour réinitialiser votre mot de passe :\n" +
    //             resetLink + "\n\nCordialement,\nChanez de l'équipe Coligo.";

    //     // Envoyer l'email
    //     emailService.sendEmail(user.getEmail(), "Réinitialisation du mot de passe", emailBody);

    //     // Retourner la réponse
    //     return ResponseEntity.ok("Un email de réinitialisation a été envoyé à l'adresse " + email);
    // }



    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("L'email est requis.");
        }

        // Rechercher l'utilisateur dans la base de données
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email non trouvé dans la base de données."));

        // Générer un token unique pour la réinitialisation
        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        userRepository.save(user);

        // Construire le lien de réinitialisation dynamiquement
        String resetLink = domainUrl + "/api/v1/reset-password?token=" + resetToken;

        // Construire le corps de l'email
        String emailBody = String.format(
            "Bonjour,\n\n" +
            "Cliquez sur le lien suivant pour réinitialiser votre mot de passe :\n" +
            "%s\n\nCordialement,\nL'équipe Coligo.",
            resetLink
        );

        // Envoyer l'email
        emailService.sendEmail(user.getEmail(), "Réinitialisation du mot de passe", emailBody);

        // Retourner la réponse
        return ResponseEntity.ok("Un email de réinitialisation a été envoyé à l'adresse " + email);
    }

    //endpoint pour changer le mot de passe 
    // @PostMapping("/reset-password")
    // public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestBody Map<String, String> request) {
    //     // Récupérer le nouveau mot de passe depuis le corps de la requête
    //     String newPassword = request.get("newPassword");
    
    //     if (newPassword == null || newPassword.trim().isEmpty()) {
    //         throw new IllegalArgumentException("Le mot de passe est requis.");
    //     }
    
    //     // Vérifier si le token est valide
    //     var user = userRepository.findByResetPasswordToken(token)
    //             .orElseThrow(() -> new RuntimeException("Token invalide ou expiré."));
    
    //     // Mettre à jour le mot de passe (avec chiffrement)
    //     user.setPassword(passwordEncoder.encode(newPassword));
    //     user.setResetPasswordToken(null); // Supprimer le token après usage
    //     userRepository.save(user);
    
    //     return ResponseEntity.ok("Mot de passe réinitialisé avec succès !");
    // }
    
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestParam("token") String token, 
            @RequestBody Map<String, String> request) {

        String newPassword = request.get("newPassword");

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Password is required."));
        }

        // Vérifiez si le token est valide
        var user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token."));

        // Mettez à jour le mot de passe avec un chiffrement
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null); // Supprimez le token après usage
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password reset successfully!"));
    }

    



}
