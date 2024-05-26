package com.jayway.msocr.contract.strategy;

import com.jayway.msocr.contract.dto.OcrResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DestinationOcrMatcher implements OcrMatcherStrategy {

    @Override
    public void apply(String text, OcrResponse ocrResponse) {
        String regex = "Destino:\\s*\\n*\\s*(\\w+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        var result = Optional.of(matcher)
                .filter(Matcher::find)
                .map(m-> m.group(1))
                .orElse("Not Found");
        ocrResponse.setDestination(result);
    }
}
