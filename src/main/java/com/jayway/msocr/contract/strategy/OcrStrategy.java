package com.jayway.msocr.contract.strategy;

import com.jayway.msocr.contract.dto.OcrRequest;

import java.io.File;

public interface OcrStrategy {
    Boolean isValid(String fileExtension);

    String apply(File file, String language);
}
