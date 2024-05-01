package com.jayway.msocr.constant;

import lombok.Getter;

@Getter
public enum OcrLanguageEnum {
    SPA("spa");
    private final String language;

    OcrLanguageEnum(String language) {
        this.language = language;
    }
}
