package com.jayway.msocr.contract.strategy;

import com.jayway.msocr.contract.dto.OcrResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SourceOcrMatcher implements OcrMatcherStrategy {
    private static final Map<String, List<String>> PATTERNS_MAP = Map.of(
            "PLIN", List.of("¡Pago exitoso!", "CJ Interbank"),
            "YAPE", List.of("¡Yapeaste!", "Estimating resolution as")
    );
    @Override
    public void apply(String text, OcrResponse ocrResponse) {
        var result = PATTERNS_MAP.entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(text::contains))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("Unknown");
        ocrResponse.setSource(result);
    }
}
