package com.jayway.msocr.contract.strategy;

import com.jayway.msocr.constant.ImageTypeEnum;
import com.jayway.msocr.contract.dto.OcrRequest;
import com.jayway.msocr.exception.OcrProcessingException;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
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
    public String apply(File file, String language) throws OcrProcessingException {
        var tesseract = new Tesseract();
        tesseract.setDatapath(getResourcePath(RESOURCES_TESSDATA));
        tesseract.setLanguage(language);
        tesseract.setPageSegMode(ONE);
        tesseract.setOcrEngineMode(ONE);
        String result;
        try {
            result = tesseract.doOCR(file);
        } catch (TesseractException e) {
            throw new OcrProcessingException("Error en el proceso de OCR con Tesseract: " + e.getLocalizedMessage());
        }
        return result;
    }

    private static String getResourcePath(String resourceDirectory) {
        URL resourceUrl = OcrImageStrategy.class.getResource(resourceDirectory);
        if (resourceUrl != null) {
            try {
                return Paths.get(resourceUrl.toURI()).toString();
            } catch (URISyntaxException e) {
                throw new RuntimeException("Error al obtener la ruta de los recursos: " + e.getLocalizedMessage());
            }
        } else {
            throw new RuntimeException("No se encontró el directorio de recursos: " + resourceDirectory);
        }
    }

}
