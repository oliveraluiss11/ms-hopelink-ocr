package com.jayway.msocr.constant;

import lombok.Getter;

@Getter
public enum ImageTypeEnum {
    JPG("jpg"),
    JPEG("jpeg"),
    PNG("png");
    private final String imageType;

    ImageTypeEnum(String imageType) {
        this.imageType = imageType;
    }
}
