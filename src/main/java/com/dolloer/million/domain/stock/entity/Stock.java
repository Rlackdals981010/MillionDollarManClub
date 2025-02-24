package com.dolloer.million.domain.stock.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String symbol; // 종목 (TSLA, NVDA, PLTR, MSTR)
    private Double highPrice; // High price of the day
    private Double lowPrice;  // Low price of the day
    private Double openPrice; // Open price of the day
    private Double previousClose; // Previous close price
    private LocalDate date;   // 날짜

    public Stock(String symbol, Double highPrice, Double lowPrice, Double openPrice, Double previousClose, LocalDate date) {
        this.symbol = symbol;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.openPrice = openPrice;
        this.previousClose = previousClose;
        this.date = date;
    }
}