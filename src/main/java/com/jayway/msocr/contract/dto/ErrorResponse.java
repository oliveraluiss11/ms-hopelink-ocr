package com.jayway.msocr.contract.dto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.jayway.msocr.util.OcrUtil.getLocalDateTime;

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

    public static ErrorResponse create(HttpStatus status, String error, String message) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setRegistrationDate(getLocalDateTime());
        errorResponse.setStatusCode(status);
        errorResponse.setError(error);
        errorResponse.setMessage(message);
        errorResponse.setPath(getRequestPath());
        return errorResponse;
    }
    private static String getRequestPath() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return request.getRequestURI();
    }
}
