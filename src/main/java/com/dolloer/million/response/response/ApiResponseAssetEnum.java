package com.dolloer.million.response.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiResponseAssetEnum implements ApiResponseEnum {
    // 200
    Asset_GET_SUCCESS(HttpStatus.OK,"전체 자산 조회에 성공하였습니다.");



    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    ApiResponseAssetEnum(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        code = httpStatus.value();
    }
}
