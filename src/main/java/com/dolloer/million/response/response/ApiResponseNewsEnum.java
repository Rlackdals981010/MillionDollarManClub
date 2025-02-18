package com.dolloer.million.response.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiResponseNewsEnum implements ApiResponseEnum {
    // 200
    NEWS_SEARCH_SUCCESS(HttpStatus.OK,"뉴스 검색에 성공하였습니다."),



    // 400
    MEMBER_COUNT_WRONG(HttpStatus.BAD_REQUEST,"10명 이하만 가능합니다.");


    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    ApiResponseNewsEnum(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        code = httpStatus.value();
    }
}
