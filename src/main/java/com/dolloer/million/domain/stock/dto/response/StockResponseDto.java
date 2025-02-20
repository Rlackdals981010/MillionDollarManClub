package com.dolloer.million.domain.stock.dto.response;

import lombok.Getter;

@Getter
public class StockResponseDto {
    private String symbol;
    private Double price;
    private Double startPrice;
    private Double priceDifference;
    private Double priceDifferencePercentage;

    public StockResponseDto(String symbol, Double price) {
        this.symbol = symbol;
        this.price = price;
    }

    public StockResponseDto(String symbol, Double price, Double startPrice, Double priceDifference, Double priceDifferencePercentage) {
        this.symbol = symbol;
        this.price = price;
        this.startPrice = startPrice;
        this.priceDifference = priceDifference;
        this.priceDifferencePercentage = priceDifferencePercentage;
    }

}
