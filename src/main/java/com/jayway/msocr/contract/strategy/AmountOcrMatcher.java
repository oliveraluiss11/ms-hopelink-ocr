package com.jayway.msocr.contract.strategy;

import com.jayway.msocr.contract.dto.OcrResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AmountOcrMatcher implements OcrMatcherStrategy {

    @Override
    public void apply(String text, OcrResponse ocrResponse) {
        String regex = "S/\\s*\\d+\\.\\d{2}|\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        var result = Optional.of(matcher)
                .filter(Matcher::find)
                .map(Matcher::group)
                .orElse("Not found");
        var valueList = List.of(result.split("\\s+"));
        var amount = BigDecimal.ZERO;
        var currencySymbol = "Unknown";
        var count = valueList.size();
        switch (count){
            case 1:
                amount = new BigDecimal(valueList.get(0));
                currencySymbol = "S/";
                break;
            case 2:
                currencySymbol = valueList.get(0);
                amount = new BigDecimal(valueList.get(1));
                break;
        }

        ocrResponse.setAmount(amount.setScale(2, RoundingMode.HALF_UP));
        ocrResponse.setCurrencySymbol(currencySymbol);
    }
}
