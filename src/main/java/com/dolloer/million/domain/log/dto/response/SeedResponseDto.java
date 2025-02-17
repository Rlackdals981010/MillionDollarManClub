package com.dolloer.million.domain.seed.dto.response;

import com.dolloer.million.domain.seed.entity.SeedHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SeedResponseDto {

    // 날짜
    private LocalDate date;

    // 추가된 시드
    private Double addedSeedMoney;

    // 토탈 시드
    private Double totalSeedMoney;

    public static SeedResponseDto from(SeedHistory seedHistory) {
        return new SeedResponseDto(seedHistory.getDate(), seedHistory.getAddedSeedMoney(),seedHistory.getTotalSeedMoney());
    }
}
