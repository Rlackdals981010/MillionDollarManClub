package com.dolloer.million.domain.stock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockResponseDto {
    private String symbol;
    private Double price;
}
