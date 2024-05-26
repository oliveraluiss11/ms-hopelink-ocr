package com.jayway.msocr.contract.strategy;

import com.jayway.msocr.contract.dto.OcrResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TransactionDateOcrMatcher implements OcrMatcherStrategy {

    @Override
    public void apply(String text, OcrResponse ocrResponse) {
        String regex = "\\d{2} [a-zA-Z]{3} \\d{4} \\d{2}:\\d{2} [aApPmM]{2}|\\d{2} [a-z]{3}\\. \\d{4} - \\d{2}:\\d{2} [aApPmM]{2}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        var result = Optional.of(matcher)
                .filter(Matcher::find)
                .map(Matcher::group)
                .orElse("Not Found");
        ocrResponse.setTransactionDate(result);
    }
}
