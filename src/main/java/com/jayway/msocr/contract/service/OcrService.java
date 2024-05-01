package com.jayway.msocr.contract.service;

import com.jayway.msocr.contract.dto.OcrRequest;
import com.jayway.msocr.contract.dto.OcrResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface OcrService {
    OcrResponse perfomOcr(MultipartFile file, OcrRequest request) throws IOException;
}
