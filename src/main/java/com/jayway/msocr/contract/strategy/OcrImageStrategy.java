package com.jayway.msocr.contract.strategy;

import com.jayway.msocr.constant.ImageTypeEnum;
import com.jayway.msocr.contract.dto.OcrRequest;
import com.jayway.msocr.exception.OcrProcessingException;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jayway.msocr.util.OcrUtil.*;

@Component
public class OcrImageStrategy implements OcrStrategy {
    @Override
    public Boolean isValid(String fileExtension) {
        return Arrays.stream(ImageTypeEnum.values())
                .anyMatch(imageTypeEnum -> imageTypeEnum.getImageType().equalsIgnoreCase(fileExtension));
    }

    @Override
    public String apply(File file, OcrRequest ocrRequest) throws OcrProcessingException {
        var tesseract = new Tesseract();
        tesseract.setDatapath(RESOURCES_TESSDATA);
        tesseract.setLanguage(ocrRequest.getLanguage());
        tesseract.setPageSegMode(ONE);
        tesseract.setOcrEngineMode(ONE);
        String result;
        try {
            result = tesseract.doOCR(file);
            Pattern pattern = Pattern.compile(OPERATION_NUMBER_REGEX);
            Matcher matcher = pattern.matcher(result);
            result = Optional.of(matcher)
                    .filter(Matcher::find)
                    .map(Matcher::group)
                    .orElse(result);
        } catch (TesseractException e) {
            throw new OcrProcessingException("Error en el proceso de OCR con Tesseract: " + e.getLocalizedMessage());
        }
        return result;
    }


}