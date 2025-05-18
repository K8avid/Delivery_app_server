package com.example.coligo.dto.request;

import java.time.LocalDate;

import lombok.Data;

import jakarta.validation.constraints.*;

@Data
public class UserRequestDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

  
    @Pattern(regexp = "^(\\+\\d{1,3})?\\d{7,15}$", message = "Please provide a valid phone number")
    private String phoneNumber; // Optionnel

    @Size(max = 100, message = "Vehicle details must not exceed 100 characters")
    private String vehicle; // Optionnel

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address; // Optionnel

    @Size(max = 100, message = "City name must not exceed 100 characters")
    private String city; // Optionnel

    @Size(max = 100, message = "Country name must not exceed 100 characters")
    private String country; // Optionnel

}
