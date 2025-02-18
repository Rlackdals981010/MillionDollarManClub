package com.dolloer.million.domain.money.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RevenueResponseDto {

    private final Double todaySavePer;
    private final Double totalRevenue;
    private final Double totalSaveMoney;
    private final Double total;
    private final Integer successQuest;
}
