package com.dolloer.million.domain.log.controller;

import com.dolloer.million.domain.log.dto.response.RevenueResponseDto;
import com.dolloer.million.domain.log.entity.RevenueHistory;
import com.dolloer.million.domain.log.service.CalendarService;
import com.dolloer.million.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
@Slf4j
public class CalendarController {

    private final CalendarService calendarService;

    // 월별 캘린더 데이터 제공
    @GetMapping
    public ResponseEntity<Page<RevenueResponseDto>> getMonthlyRevenueHistory(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam String year,
            @RequestParam String month,
            Pageable pageable) {
        Page<RevenueHistory> revenueHistories = calendarService.getMonthlyRevenueHistory(authUser.getUserId(),year, month, pageable);
        Page<RevenueResponseDto> response = revenueHistories.map(this::convertToDto);
        return ResponseEntity.ok(response);
    }

    // 특정 날짜의 상세 이력 제공
    @GetMapping("/date/{date}")
    public ResponseEntity<RevenueResponseDto> getRevenueHistoryByDate(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        RevenueHistory revenueHistory = calendarService.getRevenueHistoryByDate(authUser.getUserId(), date);
        RevenueResponseDto response = convertToDto(revenueHistory);
        return ResponseEntity.ok(response);
    }

    private RevenueResponseDto convertToDto(RevenueHistory revenueHistory) {
        return new RevenueResponseDto(
                revenueHistory.getDate(),
                revenueHistory.getAddedRevenueMoney(),
                revenueHistory.getAddedSaveMoney(),
                revenueHistory.getAddedRevenuePercent(),
                revenueHistory.getTodayTotal(),
                revenueHistory.getQuest()
        );
    }

}
