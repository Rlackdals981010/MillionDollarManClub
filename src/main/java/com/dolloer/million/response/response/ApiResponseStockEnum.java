package com.dolloer.million.response.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiResponseStockEnum implements ApiResponseEnum {
    // 200
    STOCK_GET_SUCCESS(HttpStatus.OK,"주가 조회에 성공하였습니다.");



    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    ApiResponseStockEnum(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        code = httpStatus.value();
    }
}
