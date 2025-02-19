package com.dolloer.million.domain.calendar.controller;

import com.dolloer.million.domain.calendar.dto.response.DailyQuestResponseDto;
import com.dolloer.million.domain.calendar.service.CalendarService;
import com.dolloer.million.response.response.ApiResponse;
import com.dolloer.million.response.response.ApiResponseCalendarEnum;
import com.dolloer.million.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    // 캘린더 조회
    @PostMapping
    public ResponseEntity<ApiResponse<List<DailyQuestResponseDto>>> getCalendar(@AuthenticationPrincipal AuthUser authUser, @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month){
        List<DailyQuestResponseDto> ret = calendarService.getCalendar(authUser.getUserId(),month);
        return ResponseEntity.ok(ApiResponse.success(ret, ApiResponseCalendarEnum.CALENDAR_GET_SUCCESS.getMessage()));

    }

    // 날짜 누르면 해당 날 수익 , 저축, 수익률, 전체 자산 뜨는거



}
