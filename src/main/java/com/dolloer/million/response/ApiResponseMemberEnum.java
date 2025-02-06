package com.utaemin.moyeing.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiResponseMemberEnum implements ApiResponseEnum {
    // 200
    MEMBER_CREATE_SUCCESS(HttpStatus.OK,"인원 등록에 성공하였습니다."),

    // 400
    MEMBER_COUNT_WRONG(HttpStatus.BAD_REQUEST,"10명 이하만 가능합니다.");


    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    ApiResponseMemberEnum(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        code = httpStatus.value();
    }
}
