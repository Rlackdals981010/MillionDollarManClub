package com.dolloer.million.exception;

import com.dolloer.million.exception.response.ApiResponseAuthEnum;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ApiResponseAuthEnum apiResponseAuthEnum;

    public CustomException(ApiResponseAuthEnum apiResponseAuthEnum) {
        super(apiResponseAuthEnum.getMessage());
        this.apiResponseAuthEnum = apiResponseAuthEnum;
    }
}