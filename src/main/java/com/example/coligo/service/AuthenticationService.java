package com.example.coligo.service;

import com.example.coligo.model.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.coligo.dto.request.AuthenticationRequest;
import com.example.coligo.dto.request.RegisterRequest;
import com.example.coligo.dto.response.AuthenticationResponse;
import com.example.coligo.enums.RoleName;
import com.example.coligo.repository.RoleRepository;
import com.example.coligo.repository.UserRepository;


import lombok.RequiredArgsConstructor;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    // @Value("${app.domain.url}")
    // private String domainUrl;
    @Value("")
    private String domainUrl;
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationResponse register(RegisterRequest request) {
        // Vérification de l'email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        // Récupération du rôle utilisateur
        var userRole = roleRepository.findByName(RoleName.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("Role USER not found"));

        // Génération du token de vérification d'email
        String verificationToken = UUID.randomUUID().toString();

        // Création de l'utilisateur avec tous les champs nécessaires
        var user = User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .phoneNumber(request.getPhoneNumber())
            .dateOfBirth(request.getDateOfBirth())
            .address(request.getAddress())
            .city(request.getCity())
            .country(request.getCountry())
            .vehicle(request.getVehicle())
            .emailVerificationToken(verificationToken) // Token de vérification
            .emailVerified(false) // Non vérifié par défaut
            .role(userRole) // Association du rôle utilisateur
            .build();

        // Enregistrement dans la base de données
        userRepository.save(user);

        // Envoi de l'email de vérification
        sendVerificationEmail(user);

        return AuthenticationResponse.builder()
            .token(null) // Pas de token JWT pour un compte non vérifié
            .build();
    }




    private void sendVerificationEmail(User user) {
        String verificationLink = domainUrl + "/api/v1/auth/verify?token=" + user.getEmailVerificationToken();
        String emailBody = "Bonjour " + user.getFirstName() + ",\n\n" +
            "Merci de vous être inscrit. Veuillez cliquer sur le lien ci-dessous pour vérifier votre adresse email :\n" +
            verificationLink + "\n\nCordialement,\nL'équipe Coligo.";

        emailService.sendEmail(user.getEmail(), "Vérification d'email", emailBody);
    }





    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
    
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
    
        if (!user.isEmailVerified()) {
            throw new RuntimeException("Veuillez vérifier votre email avant de vous connecter.");
        }
    
        var jwtToken = jwtService.generateToken(user);
    
        return AuthenticationResponse.builder()
            .token(jwtToken)
            .build();
    }
    
    
}
