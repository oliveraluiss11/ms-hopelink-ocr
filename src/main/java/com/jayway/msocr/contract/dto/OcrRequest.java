package com.jayway.msocr.contract.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OcrRequest {
    private String language;
    private String documentNumber;
    private String documentType;
    private String type;
}
