package com.dolloer.million.domain.stock.dto.response;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class StockLogResponseDto {
    private String symbol;
    private Double highPrice; // High price of the day
    private Double lowPrice;  // Low price of the day
    private Double openPrice; // Open price of the day
    private Double previousClose; // Previous close price
    private LocalDate date;   // 날짜

    public StockLogResponseDto(String symbol, Double highPrice, Double lowPrice, Double openPrice, Double previousClose, LocalDate date) {
        this.symbol = symbol;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.openPrice = openPrice;
        this.previousClose = previousClose;
        this.date = date;
    }

}
