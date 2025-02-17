package com.dolloer.million.domain.seed.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SeedMoneyResponseDto {

    // 날짜
    private LocalDate date;

    // 추가된 시드
    private Double addedSeedMoney;

    // 토탈 시드
    private Double totalSeedMoney;
}
