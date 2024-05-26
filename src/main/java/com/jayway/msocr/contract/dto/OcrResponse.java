package com.jayway.msocr.contract.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
public class OcrResponse implements Serializable {
    private String phone;
    private String destination;
    private String transactionDate;
    private LocalDateTime registrationDate;
    private String operationNumber;
    private BigDecimal amount;
    private String currencySymbol;
    private String source;
}
