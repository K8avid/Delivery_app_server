package com.example.coligo.mapper;

import org.springframework.stereotype.Component;

import com.example.coligo.dto.response.NotificationResponseDTO;
import com.example.coligo.model.Notification;



@Component
public class NotificationMapper {

    public NotificationResponseDTO toResponseDTO(Notification notification) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }


}
