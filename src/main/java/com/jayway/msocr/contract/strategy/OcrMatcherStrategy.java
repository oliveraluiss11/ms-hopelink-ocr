package com.jayway.msocr.contract.strategy;

import com.jayway.msocr.contract.dto.OcrResponse;

public interface OcrMatcherStrategy {
    void apply(String text, OcrResponse ocrResponse);
}
