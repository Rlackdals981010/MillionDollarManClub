package com.dolloer.million.domain.log.controller;

import com.dolloer.million.domain.log.dto.response.RevenueResponseDto;
import com.dolloer.million.domain.log.service.CalendarService;
import com.dolloer.million.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
@Slf4j
public class CalendarController {

    private final CalendarService calendarService;

    // 월별 캘린더 데이터 제공
    @GetMapping
    public ResponseEntity<Map<LocalDate, Boolean>> getMonthlyRevenueHistory(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam String year,
            @RequestParam String month) {
        log.info("캘린더 퀘스트 호출");
        Map<LocalDate, Boolean> ret = calendarService.getMonthlyQuestStatus(authUser.getUserId(),year, month);
        log.info("캘린더 퀘스트 응답");
        return ResponseEntity.ok(ret);
    }

    // 특정 날짜의 상세 이력 제공
    @GetMapping("/detail")
    public ResponseEntity<List<RevenueResponseDto>> getMonthlyRevenueDetails(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam String year,
            @RequestParam String month) {
        log.info("캘린더 상세 호출");

        List<RevenueResponseDto> ret = calendarService.getMonthlyRevenueDetails(authUser.getUserId(),year,month);
        log.info("캘린더 상세 응답");
        return ResponseEntity.ok(ret);
    }



}
