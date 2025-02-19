package com.dolloer.million.domain.calendar.dto.response;

import com.dolloer.million.domain.log.entity.RevenueHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.YearMonth;

@Getter
@AllArgsConstructor
public class DailyQuestResponseDto {
    private LocalDate date;
    private String status; // "SUCCESS", "FAIL", "FUTURE"


    public static DailyQuestResponseDto from(RevenueHistory history, YearMonth month) {
        LocalDate today = LocalDate.now();
        LocalDate historyDate = history.getDate();

        // 기준 날짜가 오늘보다 미래면 "FUTURE"
        if (historyDate.isAfter(today)) {
            return new DailyQuestResponseDto(historyDate, "FUTURE");
        }

        // 목표 수익을 달성했으면 "SUCCESS", 아니면 "FAIL"
        String status = history.getAddedRevenueMoney() >= history.getTodayTotal() ? "SUCCESS" : "FAIL";

        return new DailyQuestResponseDto(historyDate, status);
    }
}