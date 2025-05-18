package com.example.coligo.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import lombok.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateOfBirth;

    @Column(nullable = true, unique = true)
    private String phoneNumber;

    private String vehicle;
    private String address;
    private String city;
    private String country;

    private boolean accountLocked;
    private boolean accountEnabled;


    private boolean emailVerified = false;  //boolean pour dire si l'email a ete verifie ou pas

    @Column(nullable = true, unique = true)
    private String emailVerificationToken;//token de vetification de l'email

    @Column(nullable = true, unique = true)
    private String resetPasswordToken; //token pour reinitialiser le mot de passe


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(insertable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }




    // Implémentation des méthodes de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of((GrantedAuthority) () -> role.getName().name());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return accountEnabled;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
