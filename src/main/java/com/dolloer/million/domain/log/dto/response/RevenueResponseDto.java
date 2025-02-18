package com.dolloer.million.domain.log.dto.response;

import com.dolloer.million.domain.log.entity.RevenueHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class RevenueResponseDto {

    // 날짜
    private LocalDate date;

    // 추가한 수익
    private Double addedRevenueMoney;

    // 추가한 저축액
    private Double addedSaveMoney;

    // 해당 시점 수익률
    private Double addedRevenuePercent;

    // 해당 시점 총액
    private Double todayTotal;


    public static RevenueResponseDto from(RevenueHistory revenueHistory) {
        return new RevenueResponseDto(revenueHistory.getDate(),revenueHistory.getAddedRevenueMoney(), revenueHistory.getAddedSaveMoney(), revenueHistory.getAddedRevenuePercent(),revenueHistory.getTodayTotal());
    }

}
