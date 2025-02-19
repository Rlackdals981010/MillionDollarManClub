package com.dolloer.million.response.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiResponseCalendarEnum implements ApiResponseEnum {
    // 200
    CALENDAR_GET_SUCCESS(HttpStatus.OK,"달력 조회에 성공하였습니다.");



    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    ApiResponseCalendarEnum(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        code = httpStatus.value();
    }
}
