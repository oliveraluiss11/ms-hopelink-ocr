package com.jayway.msocr.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

public class OcrUtil {

    public static final String RESOURCES_TESSDATA = "src/main/resources/tessdata";
    public static final List<String> SUPPORTED_OCR_EXTENSIONS = Arrays.asList("jpeg", "jpg", "png");

    public static LocalDateTime getLocalDateTime() {
        return ZonedDateTime.now(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static final String NUMBER_REGEX = "^\\d+$";
    public static final Integer ONE = 1;
    public static final String OPERATION_NUMBER_REGEX = "\\b\\d{8}\\b";
}
