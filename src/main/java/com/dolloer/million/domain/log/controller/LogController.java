package com.dolloer.million.domain.log.controller;

import com.dolloer.million.domain.log.dto.request.RevenueRequestDto;
import com.dolloer.million.domain.log.dto.request.SaveMoneyRequestDto;
import com.dolloer.million.domain.log.dto.request.SeedRequestDto;
import com.dolloer.million.domain.log.dto.response.RevenueResponseDto;
import com.dolloer.million.domain.log.dto.response.SeedResponseDto;
import com.dolloer.million.domain.log.service.LogService;
import com.dolloer.million.response.response.ApiResponse;
import com.dolloer.million.response.response.ApiResponseLogEnum;
import com.dolloer.million.response.response.ApiResponseMoneyEnum;
import com.dolloer.million.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/log")
@Slf4j
public class LogController {

    private final LogService logService;

    // 시드 설정 / 변경
    @PostMapping("/seed")
    public ResponseEntity<ApiResponse<Void>> setSeedMoney(@AuthenticationPrincipal AuthUser authUser, @RequestBody SeedRequestDto seedMoneyRequestDto) {
        log.info("시드 설정 호출");
        logService.setSeedMoney(authUser.getUserId(), seedMoneyRequestDto.getSeedMoney());
        log.info("시드 설정 응답");
        return ResponseEntity.ok(ApiResponse.success(ApiResponseLogEnum.SEED_SET_SUCCESS.getMessage()));
    }

    // 시드 로그 조회
    @GetMapping("/seed")
    public ResponseEntity<ApiResponse<Page<SeedResponseDto>>> getSeedHistory(
            @AuthenticationPrincipal AuthUser authUser,
            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable)
    {
        log.info("시드 조회 호출");
        Page<SeedResponseDto> result = logService.getSeedHistory(authUser.getUserId(), pageable);
        log.info("시드 조회 응답");
        return ResponseEntity.ok(ApiResponse.success(result,ApiResponseLogEnum.SEED_GET_SUCCESS.getMessage()));
    }

    // 수익및 저축 설정
    @PostMapping("/revenue")
    public ResponseEntity<ApiResponse<Void>> setRevenue(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody RevenueRequestDto revenueRequestDto) {
        log.info("수익 등록 호출");
        logService.setRevenue(authUser.getUserId(), revenueRequestDto.getDailyRevenue());
        log.info("수익 등록 응답");
        return ResponseEntity.ok(ApiResponse.success(ApiResponseLogEnum.REVENUE_SET_SUCCESS.getMessage()));
    }

    @PostMapping("/save-money")
    public ResponseEntity<ApiResponse<Void>> setSaveMoney(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody SaveMoneyRequestDto saveMoneyRequestDto) {
        log.info("저축 등록 호출");
        logService.setSaveMoney(authUser.getUserId(), saveMoneyRequestDto.getDailySaveMoney());
        log.info("저축 등록 응답");
        return ResponseEntity.ok(ApiResponse.success(ApiResponseLogEnum.SAVE_MONEY_SET_SUCCESS.getMessage()));
    }

    // 수익 로그 조회
    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<Page<RevenueResponseDto>>> getRevenueHistory(
            @AuthenticationPrincipal AuthUser authUser,
            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("수익 조회 호출");
        Page<RevenueResponseDto> result = logService.getRevenueHistory(authUser.getUserId(), pageable);
        log.info("수익 조회 응답");
        return ResponseEntity.ok(ApiResponse.success(result, ApiResponseLogEnum.REVENUE_GET_SUCCESS.getMessage()));
    }



}
