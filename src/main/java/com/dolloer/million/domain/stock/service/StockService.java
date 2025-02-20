package com.dolloer.million.domain.stock.service;

import com.dolloer.million.domain.stock.dto.response.StockResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

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
                    StockResponseDto stockResponseDto = extractLatestPrice(symbol,response);
                    log.info("Symbol: {}, Price: {}", symbol, stockResponseDto.getPrice());
                    return stockResponseDto;
                });
    }

    private StockResponseDto extractLatestPrice(String symbol, String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);

            // "Time Series (1min)" 데이터 가져오기
            JsonNode timeSeries = root.path("Time Series (1min)");

            Double startPrice = null;
            Double latestPrice = null;

            Iterator<Map.Entry<String, JsonNode>> fields = timeSeries.fields();

            if (fields.hasNext()) {
                // 첫 번째 시간(장 시작 가격) 데이터 가져오기
                Map.Entry<String, JsonNode> firstEntry = fields.next();
                JsonNode startData = firstEntry.getValue();
                startPrice = startData.path("1. open").asDouble();

                // 마지막 시간(현 시점 가격) 데이터 가져오기
                Map.Entry<String, JsonNode> lastEntry = null;
                while (fields.hasNext()) {
                    lastEntry = fields.next();  // 마지막 데이터를 계속 덮어씀
                }
                if (lastEntry != null) {
                    JsonNode latestData = lastEntry.getValue();
                    latestPrice = latestData.path("4. close").asDouble();
                }
            }

            if (startPrice != null && latestPrice != null) {
                double priceDifference = latestPrice - startPrice;
                double priceDifferencePercentage = (priceDifference / startPrice) * 100;

                // String.format을 사용하여 소수점 2자리로 반올림
                String priceDifferenceFormatted = String.format("%.2f", priceDifference);
                String priceDifferencePercentageFormatted = String.format("%.2f", priceDifferencePercentage);

                log.info("Start Price: {}, Latest Price: {}, Price difference: {} , Percentage difference: {}%",
                        startPrice, latestPrice, priceDifferenceFormatted, priceDifferencePercentageFormatted);

                return new StockResponseDto(symbol, latestPrice, startPrice,
                        Double.parseDouble(priceDifferenceFormatted), Double.parseDouble(priceDifferencePercentageFormatted));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new StockResponseDto(symbol, 0.0);
    }

    public Mono<Map<String, List<Double>>> getCandleChartData(String symbol, String timeSeries, String interval) {
        String function;
        switch (timeSeries) {
            case "minute":
                function = "TIME_SERIES_INTRADAY";
                break;
            case "hour":
                function = "TIME_SERIES_INTRADAY";
                break;
            case "day":
                function = "TIME_SERIES_DAILY";
                interval = null;
                break;
            case "week":
                function = "TIME_SERIES_WEEKLY";
                interval = null;
                break;
            case "month":
                function = "TIME_SERIES_MONTHLY";
                interval = null;
                break;
            default:
                throw new IllegalArgumentException("Invalid time series type.");
        }

        String finalInterval = interval;
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("function", function)
                        .queryParam("symbol", symbol)
                        .queryParam("interval", finalInterval) // intraday만 필요
                        .queryParam("apikey", API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    log.info("Raw response from API: {}", response);
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        JsonNode root = objectMapper.readTree(response);
                        JsonNode data = root.path("Time Series " + (finalInterval != null ? "(" + finalInterval + ")" : timeSeries));
                        Map<String, List<Double>> chartData = new LinkedHashMap<>();
                        Iterator<Map.Entry<String, JsonNode>> fields = data.fields();
                        while (fields.hasNext()) {
                            Map.Entry<String, JsonNode> entry = fields.next();
                            JsonNode priceData = entry.getValue();
                            List<Double> prices = Arrays.asList(
                                    priceData.path("1. open").asDouble(),
                                    priceData.path("2. high").asDouble(),
                                    priceData.path("3. low").asDouble(),
                                    priceData.path("4. close").asDouble()
                            );
                            chartData.put(entry.getKey(), prices);
                            log.info("First data point for symbol {}: Time: {}, Prices: {}", symbol, entry.getKey(), prices);
                        }
                        return chartData;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new HashMap<>(); // 에러 발생 시 빈 맵 반환
                    }
                });
    }
}
