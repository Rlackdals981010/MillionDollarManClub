package com.dolloer.million.response.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiResponseMoneyEnum implements ApiResponseEnum {

    // 200
    SAVEPER_SET_SUCCESS(HttpStatus.OK,"저축 비율 등록에 성공하였습니다."),
    TOTAL_CAL_SUCCESS(HttpStatus.OK,"수익률 등록에 성공하였습니다."),
    QUEST_COMP_SUCCESS(HttpStatus.OK,"처리한 일퀘 등록에 성공하였습니다."),
    QUEST_UP_SUCCESS(HttpStatus.OK,"처리할 일퀘 등록에 성공하였습니다.");


    // 400



    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    ApiResponseMoneyEnum(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        code = httpStatus.value();
    }
}
