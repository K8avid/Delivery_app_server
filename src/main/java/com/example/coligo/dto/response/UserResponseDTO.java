package com.example.coligo.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String vehicle;
    private String address;
    private String city;
    private String country;
    private String role;
    private boolean accountLocked;
    private boolean accountEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}