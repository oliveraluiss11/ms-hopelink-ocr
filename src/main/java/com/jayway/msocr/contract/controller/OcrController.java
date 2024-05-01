package com.jayway.msocr.contract.controller;

import com.jayway.msocr.contract.dto.ErrorResponse;
import com.jayway.msocr.contract.dto.OcrRequest;
import com.jayway.msocr.contract.service.OcrService;
import com.jayway.msocr.exception.NoValidOcrStrategyException;
import com.jayway.msocr.exception.OcrProcessingException;
import com.jayway.msocr.exception.OperationNumberExistsException;
import com.jayway.msocr.exception.UnsupportedFileExtensionException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

import static com.jayway.msocr.util.OcrUtil.getLocalDateTime;

@RequestMapping("/v1/api/ocr")
@RestController
@RequiredArgsConstructor
@RestControllerAdvice
public class OcrController {
    public static final String REQUEST_ERROR = "Error en la solicitud";
    public static final String SERVER_ERROR_MESSAGE = "Error en el servidor";
    private final OcrService ocrService;

    @PostMapping
    public ResponseEntity<?> performOCR(
            @RequestParam("file") MultipartFile file,
            @RequestParam("language") String language,
            @RequestParam("documentNumber") String documentNumber,
            @RequestParam("documentType") String documentType,
            @RequestParam("type") String type
    ) throws IOException {
        var ocrResponse = ocrService.perfomOcr(file, OcrRequest
                .builder()
                .language(language)
                .documentNumber(documentNumber)
                .documentType(documentType)
                .type(type)
                .build());
        return ResponseEntity.ok(ocrResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, REQUEST_ERROR, ex.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({UnsupportedFileExtensionException.class,
            OperationNumberExistsException.class,
            OcrProcessingException.class,
            NoValidOcrStrategyException.class})
    public ResponseEntity<ErrorResponse> handleUnsupportedFileExtensionException(RuntimeException ex) {
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.CONFLICT, REQUEST_ERROR, ex.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleIOException(Exception ex) {
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, SERVER_ERROR_MESSAGE, ex.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String error, String message) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setRegistrationDate(getLocalDateTime());
        errorResponse.setStatusCode(status);
        errorResponse.setError(error);
        errorResponse.setMessage(message);
        errorResponse.setPath(getRequestPath());
        return errorResponse;
    }

    private String getRequestPath() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return request.getRequestURI();
    }
}
