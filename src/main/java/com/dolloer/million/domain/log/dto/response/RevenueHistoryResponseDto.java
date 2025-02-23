package com.dolloer.million.domain.log.dto.response;


import lombok.Getter;

import java.time.LocalDate;


@Getter
public class RevenueHistoryResponseDto {
    private LocalDate date;
    private Double todayTotal;
    private String name;
    private boolean isCurrentUser; // 현재 로그인한 사용자 여부

    public RevenueHistoryResponseDto(LocalDate date, Double todayTotal,String name, boolean isCurrentUser) {
        this.date = date;
        this.todayTotal = todayTotal;
        this.name = name;
        this.isCurrentUser = isCurrentUser;
    }
}

