package com.dolloer.million.domain.news.dto.response;

import lombok.Getter;

@Getter
public class NewsSentimentResponseDto {

    private String ticker;
    private String positivePercentage;
    private String negativePercentage;

    public NewsSentimentResponseDto(String ticker, String positivePercentage, String negativePercentage) {
        this.ticker = ticker;
        this.positivePercentage = positivePercentage;
        this.negativePercentage = negativePercentage;
    }
}