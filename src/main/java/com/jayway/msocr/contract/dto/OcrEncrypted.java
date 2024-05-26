package com.jayway.msocr.contract.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class OcrEncrypted {
    private String value;
    private LocalDateTime createdAt;
}
