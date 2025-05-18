package com.example.coligo.temp;


import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.coligo.enums.RoleName;
import com.example.coligo.model.Role;
import com.example.coligo.model.User;
import com.example.coligo.repository.RoleRepository;
import com.example.coligo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeUsers();
    }

    /**
     * Initialize roles in the database.
     */
    private void initializeRoles() {
        if (roleRepository.findAll().isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(RoleName.ROLE_ADMIN);
            roleRepository.save(adminRole);

            Role userRole = new Role();
            userRole.setName(RoleName.ROLE_USER);
            roleRepository.save(userRole);

            System.out.println("Roles initialized: ADMIN, USER");
        } else {
            System.out.println("Roles already exist. Skipping role initialization.");
        }
    }

    /**
     * Initialize users in the database.
     */
    private void initializeUsers() {
        if (userRepository.findAll().isEmpty()) {
            // Retrieve roles from the database
            Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin role not found"));
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("User role not found"));

            // Create Admin User
            User adminUser = new User();
            adminUser.setEmail("user3@example.com");
            adminUser.setPassword(passwordEncoder.encode("user123"));
            adminUser.setFirstName("user");
            adminUser.setLastName("three");
            adminUser.setRole(adminRole);
            adminUser.setAccountEnabled(true);
            adminUser.setAccountLocked(false);
            adminUser.setEmailVerified(true);
            userRepository.save(adminUser);

            // Create Regular User 1
            User user1 = new User();
            user1.setEmail("user1@example.com");
            user1.setPassword(passwordEncoder.encode("user123"));
            user1.setFirstName("User");
            user1.setLastName("One");
            user1.setRole(userRole);
            user1.setAccountEnabled(true);
            user1.setAccountLocked(false);
            user1.setEmailVerified(true);
            userRepository.save(user1);

            // Create Regular User 2
            User user2 = new User();
            user2.setEmail("user2@example.com");
            user2.setPassword(passwordEncoder.encode("user123"));
            user2.setFirstName("User");
            user2.setLastName("Two");
            user2.setRole(userRole);
            user2.setAccountEnabled(true);
            user2.setAccountLocked(false);
            user2.setEmailVerified(true);
            userRepository.save(user2);

            System.out.println("Users initialized: Admin, User1, User2");
        } else {
            System.out.println("Users already exist. Skipping user initialization.");
        }
    }
}
