
package com.dolloer.million.domain.log.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class UserAssetResponseDto {
    private List<RevenueHistoryResponseDto> assets;

    public UserAssetResponseDto(List<RevenueHistoryResponseDto> assets) {
        this.assets = assets;
    }
}