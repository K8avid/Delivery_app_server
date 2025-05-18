package com.example.coligo.dto.response;

import lombok.Data;
import java.time.LocalDateTime;



@Data
public class NotificationResponseDTO {
    private Long id;
    private String title;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
