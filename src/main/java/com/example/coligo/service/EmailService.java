package com.example.coligo.service;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.coligo.model.Trip;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;
import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("coligo@gmail.com");

        mailSender.send(message);
    }



    

    /**
     * Envoi d'un email HTML basé sur un template.
     *
     * @param to          Destinataire
     * @param subject     Objet de l'email
     * @param template    Nom du fichier template (sans extension)
     * @param variables   Variables dynamiques à injecter dans le template
     * @throws MessagingException
     */
    // public void sendEmail(String to, String subject, String template, Context variables) throws MessagingException {
    //     // Générer le contenu HTML à partir du template et des variables
    //     String htmlContent = templateEngine.process(template, variables);

    //     // Configurer le message email
    //     MimeMessage message = mailSender.createMimeMessage();
    //     MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    //     helper.setTo(to);
    //     helper.setSubject(subject);
    //     helper.setText(htmlContent, true);

    //     // Envoyer l'email
    //     mailSender.send(message);
    // }






    // public void sendEmail(String to, String subject, String template, Context variables) {
    //     // try {
    //     //     // Générer le contenu HTML à partir du template et des variables
    //     //     String htmlContent = templateEngine.process(template, variables);
    
    //     //     // Configurer le message email
    //     //     MimeMessage message = mailSender.createMimeMessage();
    //     //     MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    //     //     helper.setTo(to);
    //     //     helper.setSubject(subject);
    //     //     helper.setText(htmlContent, true);
    
    //     //     // Envoyer l'email
    //     //     mailSender.send(message);
    //     //     System.out.println("Email envoyé avec succès à : " + to);
    //     // } catch (Exception e) {
    //     //     // Journaliser l'erreur
    //     //     System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
    //     //     e.printStackTrace();
    //     // }



    //     MimeMessage mimeMessage = mailSender.createMimeMessage();
    //     MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
    //     try {
    //         helper.setSubject("Merci pour votre commande");
    //         helper.setTo(order.getUser().getEmail());

    //         Context context = new Context();
    //         context.setVariable("order", order);
    //         String emailContent = templateEngine.process("order_thank_you_template", context);

    //         helper.setText(emailContent, true);

    //         mailSender.send(mimeMessage);
    //     } catch (MessagingException e) {
    //         e.printStackTrace();
    //     }
    // }
    


    // public void sendEmailWithAttachment(String to, String subject, String htmlContent, byte[] attachmentData, String attachmentName) throws MessagingException {
    //     // Créer un message MIME
    //     MimeMessage mimeMessage = mailSender.createMimeMessage();

    //     // Configurer le MimeMessageHelper
    //     MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED, StandardCharsets.UTF_8.name());
    //     helper.setTo(to);
    //     helper.setSubject(subject);
    //     helper.setText(htmlContent, true); // true pour activer le contenu HTML

    //     // Ajouter une pièce jointe si nécessaire
    //     if (attachmentData != null && attachmentName != null) {
    //         helper.addAttachment(attachmentName, new ByteArrayDataSource(attachmentData, "application/octet-stream"));
    //     }

    //     // Envoyer l'email
    //     mailSender.send(mimeMessage);
    // }


    


    public void sendHtmlEmailWithTemplate(String to, String subject, String template, Context variables) {
        try {

            // Générer le contenu HTML à partir du template
            String htmlContent = templateEngine.process(template, variables);

            // Créer et configurer MimeMessage
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MULTIPART_MODE_MIXED_RELATED, UTF_8.name());
            helper.setFrom("local@email.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indique qu'il s'agit de contenu HTML

        
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }



    //  public void sendTripEmail(String to, Trip trip) {
    //     // Préparer les variablls dynamiques
    //     Context context = new Context();
    //     context.setVariable("departureLocation", trip.getStartLocation().getName());
    //     context.setVariable("arrivalLocation", trip.getEndLocation().getName());
    //     context.setVariable("departureDate", trip.getDepartureTime().toString());
    //     context.setVariable("arrivalDate", trip.getDepartureTime().plusMinutes(trip.getDuration()).toString());
    //     context.setVariable("duration", trip.getDuration());
    //     context.setVariable("distance", trip.getDistance());

    //     // Nom du fichier template (dans src/main/resources/templates)
    //     String template = "trip-email-template";

    //     // Envoyer l'email
    //     try {
    //         emailService.sendHtmlEmailWithTemplate(to, "Nouveau Trajet Disponible", template, context);
    //     } catch (MessagingException e) {
    //         e.printStackTrace();
    //     }
    // }
}
