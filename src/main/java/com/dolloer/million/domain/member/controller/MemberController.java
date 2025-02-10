package com.dolloer.million.domain.member.controller;

import com.dolloer.million.domain.member.dto.request.QuestRequestDto;
import com.dolloer.million.domain.member.dto.request.RevenueRequestDto;
import com.dolloer.million.domain.member.dto.request.SeedMoneyRequestDto;
import com.dolloer.million.domain.member.dto.response.CalculateResponseDto;
import com.dolloer.million.domain.member.dto.response.RevenueResponseDto;
import com.dolloer.million.domain.member.dto.response.UpcomingQuestResponseDto;
import com.dolloer.million.domain.member.service.MemberService;
import com.dolloer.million.response.response.ApiResponse;
import com.dolloer.million.response.response.ApiResponseMemberEnum;
import com.dolloer.million.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goal")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    // 시드 설정 / 변경
    @PostMapping("/seed")
    public ResponseEntity<ApiResponse<Void>> setSeedMoney(@AuthenticationPrincipal AuthUser authUser, @RequestBody SeedMoneyRequestDto seedMoneyRequestDto) {
        memberService.setSeedMoney(authUser.getUserId(), seedMoneyRequestDto.getSeedMoney());
        return ResponseEntity.ok(ApiResponse.success(ApiResponseMemberEnum.SEED_SET_SUCCESS.getMessage()));
    }

    // 수입과 저축할 돈 입력
    @PostMapping("/revenue")
    public ResponseEntity<ApiResponse<RevenueResponseDto>> setRevenue(@AuthenticationPrincipal AuthUser authUser,@RequestBody RevenueRequestDto revenueRequestDto){
        return ResponseEntity.ok(ApiResponse.success(memberService.setRevenue(authUser.getUserId(), revenueRequestDto.getDailyRevenue(),revenueRequestDto.getDailySaveMoney()), ApiResponseMemberEnum.REVENUE_SET_SUCCESS.getMessage()));
    }

    // 시드 대비 총 수익률 계산
    @GetMapping("/revenue/calculate")
    public ResponseEntity<ApiResponse<CalculateResponseDto>> calculateTotalReturn(@AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(ApiResponse.success(memberService.calculateTotalReturn(authUser.getUserId()), ApiResponseMemberEnum.TOTAL_CAL_SUCCESS.getMessage()));
    }


    // 처리할 일퀘
    @GetMapping("/quest/upcoming")
    public ResponseEntity<ApiResponse<UpcomingQuestResponseDto>> upcomingQuest(@AuthenticationPrincipal AuthUser authUser, @RequestBody QuestRequestDto questRequestDto){
        return ResponseEntity.ok(ApiResponse.success(memberService.upcomingQuest(authUser.getUserId(),questRequestDto.getPer()), ApiResponseMemberEnum.QUEST_UP_SUCCESS.getMessage()));
    }
}
