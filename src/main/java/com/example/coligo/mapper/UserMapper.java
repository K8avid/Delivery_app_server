package com.example.coligo.mapper;
import org.springframework.stereotype.Component;

import com.example.coligo.dto.request.UserRequestDTO;
import com.example.coligo.dto.response.UserResponseDTO;
import com.example.coligo.model.Role;
import com.example.coligo.model.User;





@Component
public class UserMapper {

    // Convertir UserRequestDTO en entité User
    public User toEntity(UserRequestDTO dto, Role role) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setVehicle(dto.getVehicle());
        user.setAddress(dto.getAddress());
        user.setCity(dto.getCity());
        user.setCountry(dto.getCountry());
        user.setRole(role);
        user.setAccountEnabled(true);
        user.setAccountLocked(false);
        return user;
    }

    // Convertir entité User en UserResponseDTO
    public UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setVehicle(user.getVehicle());
        dto.setAddress(user.getAddress());
        dto.setCity(user.getCity());
        dto.setCountry(user.getCountry());
        dto.setRole(user.getRole().getName().name());
        dto.setAccountLocked(user.isAccountLocked());
        dto.setAccountEnabled(user.isAccountEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}