package com.dolloer.million.domain.seed.controller;

import com.dolloer.million.domain.seed.dto.request.SeedMoneyRequestDto;
import com.dolloer.million.domain.seed.service.MemberService;
import com.dolloer.million.response.response.ApiResponse;
import com.dolloer.million.response.response.ApiResponseMemberEnum;
import com.dolloer.million.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/set")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    // 시드 설정 / 변경
    @PostMapping("/seed")
    public ResponseEntity<ApiResponse<Void>> setSeedMoney(@AuthenticationPrincipal AuthUser authUser, @RequestBody SeedMoneyRequestDto seedMoneyRequestDto) {
        memberService.setSeedMoney(authUser.getUserId(), seedMoneyRequestDto.getSeedMoney());
        return ResponseEntity.ok(ApiResponse.success(ApiResponseMemberEnum.SEED_SET_SUCCESS.getMessage()));
    }


}
