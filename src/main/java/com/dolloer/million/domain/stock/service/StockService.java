package com.dolloer.million.domain.stock.service;

import com.dolloer.million.domain.stock.dto.response.StockResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.Map;

@Service
@Slf4j
public class StockService {

    private final WebClient webClient;

    @Value("${ALPHA_VANTAGE_API_KEY}")
    private String API_KEY;

    public StockService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://www.alphavantage.co/query").build();
    }

    public Mono<StockResponseDto> getStockPrice(String symbol) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("function", "TIME_SERIES_INTRADAY")
                        .queryParam("symbol", symbol)
                        .queryParam("interval", "1min")
                        .queryParam("apikey", API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    Double price = extractLatestPrice(response);
                    log.info("Symbol: {}, Price: {}", symbol, price);
                    return new StockResponseDto(symbol, price);
                });
    }

    private Double extractLatestPrice(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);

            // "Time Series (1min)" 데이터 가져오기
            JsonNode timeSeries = root.path("Time Series (1min)");

            // 최신 데이터를 가져오기 위해 첫 번째 키를 찾음
            Iterator<Map.Entry<String, JsonNode>> fields = timeSeries.fields();
            if (fields.hasNext()) {
                Map.Entry<String, JsonNode> firstEntry = fields.next();
                JsonNode latestData = firstEntry.getValue();

                return latestData.path("4. close").asDouble();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
