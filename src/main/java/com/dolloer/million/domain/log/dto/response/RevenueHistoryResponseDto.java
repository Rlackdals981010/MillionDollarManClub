package com.dolloer.million.domain.log.dto.response;


import lombok.Getter;

import java.time.LocalDate;


@Getter
public class RevenueHistoryResponseDto {
    private LocalDate date;
    private Double todayTotal;
    private String name;


    public RevenueHistoryResponseDto(LocalDate date, Double todayTotal,String name) {
        this.date = date;
        this.todayTotal = todayTotal;
        this.name = name;
    }
}

