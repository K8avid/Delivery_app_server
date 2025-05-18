package com.example.coligo.dto.response;

import lombok.Data;

// @Data
// public class ApiResponse<T> {
//     private String message;
//     private int status;
//     private T data;
// }




import java.time.LocalDateTime;


@Data
public class ApiResponse<T> {
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private Object errorDetails;
}
