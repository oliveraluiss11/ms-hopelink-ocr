package com.jayway.msocr.contract.service;

import com.jayway.msocr.contract.dto.OcrEncrypted;
import com.jayway.msocr.contract.dto.OcrResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface OcrService {
    OcrEncrypted perfomOcr(MultipartFile file, String language) throws Exception;
}
