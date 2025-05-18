package com.example.coligo.service;

import org.springframework.stereotype.Service;

import com.example.coligo.dto.response.UserResponseDTO;
import com.example.coligo.model.User;
import com.example.coligo.repository.RoleRepository;
import com.example.coligo.repository.UserRepository;
import com.example.coligo.dto.request.UserRequestDTO;
import com.example.coligo.enums.RoleName;
import com.example.coligo.mapper.UserMapper;
import com.example.coligo.model.Location;
import com.example.coligo.model.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;






@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserMapper userMapper;


    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }



    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toResponseDTO(user);
    }


    
    public void saveUser(User user) {
        userRepository.save(user);
    }




    @Transactional
    public UserResponseDTO createUser(UserRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        Role defaultRole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new RuntimeException("Le rôle ROLE_USER est introuvable"));
        String encodedPassword = new BCryptPasswordEncoder().encode(dto.getPassword());
        dto.setPassword(encodedPassword);
        User user = userMapper.toEntity(dto, defaultRole);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }


  
  

    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

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

        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDTO(updatedUser);
    }




    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }


    public User getCurrentAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        }

        throw new RuntimeException("No authenticated user found");
    }



    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }



    // public List<User> findInterestedSenders(Location startLocation, Location endLocation, double radius) {
    //     // Exemple de logique : Trouver les expéditeurs ayant des colis proches des localisations du trajet
    //     return userRepository.findAll().stream()
    //             .filter(user -> user.hasParcels())
    //             .filter(user -> user.getParcels().stream().anyMatch(parcel -> 
    //                     locationService.calculateDistance(parcel.getSenderAddressLocation(), startLocation) <= radius &&
    //                     locationService.calculateDistance(parcel.getRecipientAddressLocation(), endLocation) <= radius))
    //             .collect(Collectors.toList());
    // }
    
    
}




// @Service
// public class UserService {
//     @Autowired
//     private UserRepository userRepository;

//     @Autowired
//     private RoleRepository roleRepository;

//     @Autowired
//     private UserMapper userMapper;


//     public List<UserResponseDTO> getAllUsers() {
//         return userRepository.findAll().stream()
//                 .map(userMapper::toResponseDTO)
//                 .collect(Collectors.toList());
//     }



//     public UserResponseDTO getUserById(Long id) {
//         User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
//         return userMapper.toResponseDTO(user);
//     }



//     @Transactional
//     public UserResponseDTO createUser(UserRequestDTO dto) {

//         if (userRepository.existsByEmail(dto.getEmail())) {
//             throw new RuntimeException("Cet email est déjà utilisé");
//         }

//         Role defaultRole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new RuntimeException("Le rôle ROLE_USER est introuvable"));
//         String encodedPassword = new BCryptPasswordEncoder().encode(dto.getPassword());
//         dto.setPassword(encodedPassword);
//         User user = userMapper.toEntity(dto, defaultRole);
//         User savedUser = userRepository.save(user);
//         return userMapper.toResponseDTO(savedUser);
//     }
    
//     public void saveUser(User user) {
//         userRepository.save(user);
//     }

  
  

//     public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
//         User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

//         user.setEmail(dto.getEmail());
//         user.setPassword(dto.getPassword());
//         user.setFirstName(dto.getFirstName());
//         user.setLastName(dto.getLastName());
//         user.setDateOfBirth(dto.getDateOfBirth());
//         user.setPhoneNumber(dto.getPhoneNumber());
//         user.setVehicle(dto.getVehicle());
//         user.setAddress(dto.getAddress());
//         user.setCity(dto.getCity());
//         user.setCountry(dto.getCountry());

//         User updatedUser = userRepository.save(user);
//         return userMapper.toResponseDTO(updatedUser);
//     }




//     public void deleteUserId(Long id) {
//         if (!userRepository.existsById(id)) {
//             throw new RuntimeException("User not found");
//         }
//         userRepository.deleteById(id);
//     }

//     public void deleteUser(User user) {
//         try {
//             userRepository.delete(user); // Supprimer l'utilisateur de la base de données
//         } catch (Exception e) {
//             throw new RuntimeException("Erreur lors de la suppression de l'utilisateur", e);
//         }
//     }
    

//     public User getCurrentAuthenticatedUser() {
//     //     // Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

//     //     // if (principal instanceof UserDetails) {
//     //     //     String email = ((UserDetails) principal).getUsername();
//     //     //     // return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Authenticated user not found"));
//             return getUserByEmail("user1@example.com");
//     //     // }

//     //     // throw new RuntimeException("No authenticated user found");
//     }
    


//     public User getUserByEmail(String email) {
//         return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email));
//     }
// }