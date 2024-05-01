package com.jayway.msocr.contract.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ErrorResponse {
    private LocalDateTime registrationDate;
    private String path;
    private HttpStatus statusCode;
    private String error;
    private String message;
}
