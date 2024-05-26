package com.jayway.msocr.contract.service;

import com.jayway.msocr.constant.OcrLanguageEnum;
import com.jayway.msocr.contract.dto.OcrEncrypted;
import com.jayway.msocr.contract.dto.OcrResponse;
import com.jayway.msocr.contract.strategy.OcrMatcherStrategy;
import com.jayway.msocr.contract.strategy.OcrStrategy;
import com.jayway.msocr.exception.NoValidOcrStrategyException;
import com.jayway.msocr.util.ConvertUtil;
import com.jayway.msocr.util.OcrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrServiceImpl implements OcrService {
    private final List<OcrStrategy> ocrStrategyList;
    private final List<OcrMatcherStrategy> ocrMatcherStrategies;
    private final Environment environment;

    @Override
    public OcrEncrypted perfomOcr(MultipartFile file, String language) throws Exception {
        this.ensureOcrLanguageIsValid(language);
        var fileExtension = getFileExtension(file);
        this.ensureFileExtensionIsValid(fileExtension);
        var convertedFile = ConvertUtil.convertMultiPartToFile(file);
        var text = ocrStrategyList.stream()
                .filter(ocrStrategy -> ocrStrategy.isValid(fileExtension))
                .map(ocrStrategy -> ocrStrategy.apply(convertedFile, language))
                .findFirst()
                .orElseThrow(() ->
                        new NoValidOcrStrategyException("No se encontró una estrategia válida para OCR"));
        var ocrResponse = OcrResponse
                .builder()
                .registrationDate(LocalDateTime.now(ZoneId.systemDefault()))
                .build();
        ocrMatcherStrategies.forEach(ocrMatcherStrategy -> ocrMatcherStrategy.apply(text, ocrResponse));
        var textEncrypted = this.encrypt(ocrResponse);
        return OcrEncrypted.builder().value(textEncrypted).createdAt(LocalDateTime.now()).build();
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

    private void ensureOcrLanguageIsValid(String value) {
        if (Arrays.stream(OcrLanguageEnum.values())
                .noneMatch(enumValue -> enumValue.getLanguage().equalsIgnoreCase(value))) {
            throw new IllegalArgumentException("Lenguaje de OCR no válido: " + value);
        }
    }

    public String encrypt(OcrResponse ocrResponse) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(ocrResponse);
        byte[] objectBytes = outputStream.toByteArray();

        SecretKey secretKey = getSecretKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(objectBytes);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private SecretKey getSecretKey() {
        String key = environment.getProperty("ocr.key");
        byte[] decode = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decode, "AES");
    }

    private OcrResponse decrypt(String encryptedText) throws Exception {

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(decryptedBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        return (OcrResponse) objectInputStream.readObject();
    }
}
