package com.example.coligo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import com.example.coligo.dto.response.NotificationResponseDTO;
import com.example.coligo.enums.NotificationType;
import com.example.coligo.mapper.DeliveryMapper;
import com.example.coligo.mapper.NotificationMapper;
import com.example.coligo.model.Notification;
import com.example.coligo.model.User;
import com.example.coligo.repository.NotificationRepository;

import java.util.List;
import java.util.stream.Collectors;



@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    

     // Récupérer les notifications d'un utilisateur
    public List<NotificationResponseDTO> getUserNotifications() {
        User currenUser = userService.getCurrentAuthenticatedUser();
        return notificationRepository.findByTargetUserId(currenUser.getId()).stream()
                .map(notificationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

   

    // Marquer une notification comme lue
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }





    // Méthode pour créer une notification
    public void createNotification(NotificationType type, User targetUser, String title, String message) {
        if (targetUser == null) {
            throw new IllegalArgumentException("Target user cannot be null");
        }

        Notification notification = new Notification();
        notification.setTargetUser(targetUser);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setIsRead(false);
        notificationRepository.save(notification);

    }


    // Méthode pour créer une notification
    public void createNotification(User targetUser, String title, String message) {
        if (targetUser == null) {
            throw new IllegalArgumentException("Target user cannot be null");
        }

        Notification notification = new Notification();
        notification.setTargetUser(targetUser);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setIsRead(false);
        Notification savedNotification = notificationRepository.save(notification);
        sendNotificationEmailWithTemplate(notification);
    }



    @Async
    public void sendNotificationEmailWithTemplate(Notification notification) {
        User targetUser = notification.getTargetUser();

        if (targetUser == null || targetUser.getEmail() == null || targetUser.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Target user email is not valid");
        }

        // Définir les variables pour le template
        Context context = new Context();
        context.setVariable("userName", targetUser.getFullName());
        context.setVariable("title", notification.getTitle());
        context.setVariable("message", notification.getMessage());
        context.setVariable("footer", "Cet email est généré automatiquement. Veuillez ne pas répondre.");

        // Appeler la méthode d'email avec template
        emailService.sendHtmlEmailWithTemplate(
            targetUser.getEmail(),
            notification.getTitle(),
            "notif-email-template", // Nom du fichier template (par ex. resources/templates/email-template.html)
            context
        );
    }



    
  


    public void notifyGeneralInfo(User user, String title, String message) {
        createNotification(
            NotificationType.GENERAL_INFO,
            user,
            title,
            message
        );
    }




    
    public void notifyClientForDriverResponse(User client, String tripNumber, String driverName, boolean accepted) {
        String title = accepted 
                ? "Demande de livraison acceptée" 
                : "Demande de livraison refusée";
        
        String message = accepted 
                ? "Le conducteur " + driverName + " a accepté votre demande pour le trajet (" + tripNumber + ")."
                : "Le conducteur " + driverName + " a refusé votre demande pour le trajet (" + tripNumber + ").";
    
        createNotification(
                NotificationType.GENERAL_INFO,
                client,
                title,
                message
        );
    }
    
    
    
}
