package com.dolloer.million.domain.auth.controller;

import com.dolloer.million.domain.auth.dto.request.AuthRequestDto;
import com.dolloer.million.domain.auth.dto.response.SignInResponseDto;
import com.dolloer.million.domain.auth.dto.response.SignUpResponseDto;
import com.dolloer.million.domain.auth.service.AuthService;
import com.dolloer.million.response.response.ApiResponse;
import com.dolloer.million.response.response.ApiResponseAuthEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponseDto>> signUp(@RequestBody AuthRequestDto authRequestDto){
        return ResponseEntity.ok(ApiResponse.success(authService.signUp(authRequestDto.getName()), ApiResponseAuthEnum.MEMBER_CREATE_SUCCESS.getMessage()));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<SignInResponseDto>> signIn(@RequestBody AuthRequestDto authRequestDto){
        log.info("로그인 호출");
        return ResponseEntity.ok(ApiResponse.success(authService.signIn(authRequestDto.getName()), ApiResponseAuthEnum.MEMBER_LOGIN_SUCCESS.getMessage()));

    }
}
