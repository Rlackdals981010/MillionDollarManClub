package com.dolloer.million.response.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiResponseStockEnum implements ApiResponseEnum {
    // 200
    STOCK_GET_SUCCESS(HttpStatus.OK,"주가 조회에 성공하였습니다."),
    CANDLE_DATA_SUCCESS(HttpStatus.OK,"주가 전체 조회에 성공하였습니다."),
    STOCK_HISTORY_SUCCESS(HttpStatus.OK, "주가 기록 조회에 성공하였습니다."),
    STOCK_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "잘못된 티커입니다.");


    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    ApiResponseStockEnum(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        code = httpStatus.value();
    }
}
