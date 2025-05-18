package com.example.coligo.dto.request;



import java.time.LocalDate;

// import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class UserUpdateRequest {
    private String firstName;

    private String lastName;

    private String email;
    private LocalDate  dateOfBirth;
    private String phoneNumber;
    private String address;
    private String city;
    private String country;
    private String vehicle;
}

   