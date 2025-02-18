package com.dolloer.million.domain.log.controller;

import com.dolloer.million.domain.log.dto.request.RevenueRequestDto;
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
        logService.setSeedMoney(authUser.getUserId(), seedMoneyRequestDto.getSeedMoney());
        return ResponseEntity.ok(ApiResponse.success(ApiResponseLogEnum.SEED_SET_SUCCESS.getMessage()));
    }

    // 시드 로그 조회
    @GetMapping("/seed")
    public ResponseEntity<ApiResponse<Page<SeedResponseDto>>> getSeedHistory(
            @AuthenticationPrincipal AuthUser authUser,
            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable)
    {
        Page<SeedResponseDto> result = logService.getSeedHistory(authUser.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(result,ApiResponseLogEnum.SEED_GET_SUCCESS.getMessage()));
    }

    // 수익및 저축 설정
    @PostMapping("/revenue")
    public ResponseEntity<ApiResponse<Void>> setRevenue(@AuthenticationPrincipal AuthUser authUser, @RequestBody RevenueRequestDto revenueRequestDto){
        logService.setRevenueMoney(authUser.getUserId(), revenueRequestDto.getDailyRevenue(), revenueRequestDto.getDailySaveMoney());
        return ResponseEntity.ok(ApiResponse.success( ApiResponseLogEnum.REVENUE_SET_SUCCESS.getMessage()));
    }

    // 수익 로그 조회
    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<Page<RevenueResponseDto>>> getRevenueHistory(
            @AuthenticationPrincipal AuthUser authUser,
            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<RevenueResponseDto> result = logService.getRevenueHistory(authUser.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(result, ApiResponseLogEnum.REVENUE_GET_SUCCESS.getMessage()));
    }



}
