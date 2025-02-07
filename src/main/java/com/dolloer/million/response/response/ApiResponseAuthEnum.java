package com.dolloer.million.exception.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiResponseAuthEnum implements ApiResponseEnum {
    // 200
    MEMBER_CREATE_SUCCESS(HttpStatus.OK,"인원 등록에 성공하였습니다."),
    MEMBER_LOGIN_SUCCESS(HttpStatus.OK,"로그인에 성공하였습니다."),

    // 400
    MEMBER_ALREADY_EXIST(HttpStatus.BAD_REQUEST,"이미 가입되어있지롱"),
    MEMBER_NO_EXIST(HttpStatus.BAD_REQUEST,"등록해달라하셈");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    ApiResponseAuthEnum(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        code = httpStatus.value();
    }
}
