package com.example.coligo.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {
    private String errorInternalCode;
    private String errorMessage;
    private LocalDateTime timestamp;
    private Map<String, String> errors;
}
