package com.jayway.msocr.contract.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "ocr")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OcrDocument {
    @Id
    private String ocrId;
    private String operationNumber;
    private String language;
    private String type;
    private String documentNumber;
    private String documentType;
    private LocalDateTime registrationDate;
    private LocalDateTime updatedDate;
    private String state;
}
