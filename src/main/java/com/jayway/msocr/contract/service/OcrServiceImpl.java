package com.jayway.msocr.contract.service;

import com.jayway.msocr.constant.DocumentTypeEnum;
import com.jayway.msocr.constant.OcrLanguageEnum;
import com.jayway.msocr.constant.OcrTypeEnum;
import com.jayway.msocr.contract.dto.OcrRequest;
import com.jayway.msocr.contract.dto.OcrResponse;
import com.jayway.msocr.contract.entity.OcrDocument;
import com.jayway.msocr.contract.repository.OcrRepository;
import com.jayway.msocr.contract.strategy.OcrStrategy;
import com.jayway.msocr.exception.NoValidOcrStrategyException;
import com.jayway.msocr.exception.OperationNumberExistsException;
import com.jayway.msocr.util.ConvertUtil;
import com.jayway.msocr.util.OcrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.jayway.msocr.util.OcrUtil.NUMBER_REGEX;
import static com.jayway.msocr.util.OcrUtil.getLocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrServiceImpl implements OcrService {
    public static final String ACTIVE = "ACTIVE";
    private final List<OcrStrategy> ocrStrategyList;
    private final OcrRepository ocrRepository;

    @Override
    public OcrResponse perfomOcr(MultipartFile file, OcrRequest request) throws IOException {
        this.ensureDocumentTypeIsValid(request.getDocumentType());
        this.ensureDocumentNumberIsValid(request.getDocumentNumber());
        this.ensureOcrTypeIsValid(request.getType());
        this.ensureOcrLanguageIsValid(request.getLanguage());
        var fileExtension = getFileExtension(file);
        this.ensureFileExtensionIsValid(fileExtension);
        var convertedFile = ConvertUtil.convertMultiPartToFile(file);
        var operationNumber = ocrStrategyList.stream()
                .filter(ocrStrategy -> ocrStrategy.isValid(fileExtension))
                .map(ocrStrategy -> ocrStrategy.apply(convertedFile, request))
                .findFirst()
                .orElseThrow(() ->
                        new NoValidOcrStrategyException("No se encontró una estrategia válida para OCR"));
        this.ensureOcrDocumentNotExists(request.getDocumentNumber(), operationNumber);
        this.saveOcr(request, operationNumber);
        return OcrResponse
                .builder()
                .registrationDate(getLocalDateTime())
                .result(operationNumber)
                .build();
    }

    private void saveOcr(OcrRequest request, String operationNumber) {
        var ocrDocument = OcrDocument
                .builder()
                .documentNumber(request.getDocumentNumber())
                .documentType(request.getDocumentType())
                .operationNumber(operationNumber)
                .language(request.getLanguage())
                .state(ACTIVE)
                .type(request.getType())
                .registrationDate(getLocalDateTime())
                .build();
        var documentSaved = this.ocrRepository.save(ocrDocument);
        log.info("Document Saved: {}", documentSaved);
    }

    private String getFileExtension(MultipartFile file) {
        var originalFilename = file.getOriginalFilename();
        return Optional.ofNullable(originalFilename)
                .map(fileName -> fileName.lastIndexOf("."))
                .filter(lastDotIndex -> lastDotIndex > 0)
                .map(lastDotIndex -> originalFilename.substring(lastDotIndex + 1))
                .orElseThrow(() -> new IllegalArgumentException("El nombre de archivo original no debe ser nulo."));
    }

    private void ensureFileExtensionIsValid(String value) {
        if (!OcrUtil.SUPPORTED_OCR_EXTENSIONS.contains(value)) {
            throw new IllegalArgumentException("Extensión de archivo no válida: " + value);
        }
    }

    private void ensureOcrTypeIsValid(String value) {
        if (Arrays.stream(OcrTypeEnum.values())
                .noneMatch(enumValue -> enumValue.name().equalsIgnoreCase(value))) {
            throw new IllegalArgumentException("Tipo de OCR no válido: " + value);
        }
    }

    private void ensureDocumentTypeIsValid(String value) {
        if (Arrays.stream(DocumentTypeEnum.values())
                .noneMatch(enumValue -> enumValue.name().equalsIgnoreCase(value))) {
            throw new IllegalArgumentException("Tipo de documento no válido: " + value);
        }
    }

    private void ensureOcrLanguageIsValid(String value) {
        if (Arrays.stream(OcrLanguageEnum.values())
                .noneMatch(enumValue -> enumValue.getLanguage().equalsIgnoreCase(value))) {
            throw new IllegalArgumentException("Lenguaje de OCR no válido: " + value);
        }
    }

    private void ensureDocumentNumberIsValid(String value) {
        if (!Pattern.matches(NUMBER_REGEX, value)) {
            throw new IllegalArgumentException("Número de documento no válido: " + value);
        }
    }

    private void ensureOcrDocumentNotExists(String documentNumber, String operationNumber) {
        var exists = this.ocrRepository
                .existsByDocumentNumberAndOperationNumber(documentNumber, operationNumber);
        if (exists) throw new OperationNumberExistsException("El número de operación ya existe en la base de datos: "
                + operationNumber);
    }
}
