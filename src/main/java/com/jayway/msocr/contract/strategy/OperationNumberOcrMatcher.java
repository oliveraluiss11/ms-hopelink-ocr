package com.jayway.msocr.contract.strategy;

import com.jayway.msocr.contract.dto.OcrResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OperationNumberOcrMatcher implements OcrMatcherStrategy {

    @Override
    public void apply(String text, OcrResponse ocrResponse) {
        String regex = "\\b\\d{8}\\b";;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        var result = Optional.of(matcher)
                .filter(Matcher::find)
                .map(Matcher::group)
                .orElse("Not found");
        ocrResponse.setOperationNumber(result);
    }
}
