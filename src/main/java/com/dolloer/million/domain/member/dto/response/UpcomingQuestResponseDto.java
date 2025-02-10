package com.dolloer.million.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpcomingQuestResponseDto {
    private final double finalMoney;
    private final double saveMoney;
    private final Integer count;
}
