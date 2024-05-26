package com.jayway.msocr.contract.strategy;

import com.jayway.msocr.contract.dto.OcrResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Component
public class PhoneOcrMatcher implements OcrMatcherStrategy {

    @Override
    public void apply(String text, OcrResponse ocrResponse) {
        String regex = "[0-9*]{3}\\s+[0-9*]{3}\\s+[0-9*]{3}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        var result = Optional.of(matcher)
                .filter(Matcher::find)
                .map(Matcher::group)
                .orElse("Not found");
        ocrResponse.setPhone(result);
    }
}
