package com.dolloer.million.response.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiResponseLogEnum implements ApiResponseEnum {
    // 200
    SEED_SET_SUCCESS(HttpStatus.OK,"시드 등록에 성공하였습니다."),
    REVENUE_SET_SUCCESS(HttpStatus.OK,"수익 등록에 성공하였습니다."),
    SEED_GET_SUCCESS(HttpStatus.OK,"시드 로그 조회에 성공하였습니다."),
    REVENUE_GET_SUCCESS(HttpStatus.OK,"수익 로그 조회에 성공하였습니다."),
    // 400
    MEMBER_COUNT_WRONG(HttpStatus.BAD_REQUEST,"10명 이하만 가능합니다.");


    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    ApiResponseLogEnum(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        code = httpStatus.value();
    }
}
