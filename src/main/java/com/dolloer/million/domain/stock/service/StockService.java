package com.dolloer.million.domain.stock.service;

import com.dolloer.million.domain.stock.dto.response.StockLogResponseDto;
import com.dolloer.million.domain.stock.dto.response.StockResponseDto;
import com.dolloer.million.domain.stock.entity.Stock;
import com.dolloer.million.domain.stock.repository.StockRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StockService {

    private final StockRepository stockRepository;
    private final WebClient webClient;

    @Value("${FINN_HUB_API_KEY}")
    private String API_KEY;

    public StockService(StockRepository stockRepository, WebClient.Builder webClientBuilder) {
        this.stockRepository = stockRepository;
        this.webClient = webClientBuilder.baseUrl("https://finnhub.io/api/v1").build();
    }

    // 매일 PST 오후 1시 (미국 주식 마감 시간)에 실행
    @Scheduled(cron = "0 0 13 * * MON-FRI", zone = "America/Los_Angeles")
    private void fetchAndSaveDailyStockPrices() {
        List<String> symbols = Arrays.asList("TSLA", "NVDA", "PLTR", "MSTR");

        symbols.forEach(symbol -> {
            getStockPriceForLog(symbol)
                    .map(dto -> new Stock(
                            symbol,
                            dto.getHighPrice(),
                            dto.getLowPrice(),
                            dto.getOpenPrice(),
                            dto.getPreviousClose(),
                            LocalDate.now()
                    ))
                    .subscribe(stock -> {
                        stockRepository.save(stock);
                        log.info("Saved stock price for {}: High={}, Low={}, Open={}, PreviousClose={}",
                                symbol, stock.getHighPrice(), stock.getLowPrice(),
                                stock.getOpenPrice(), stock.getPreviousClose());
                    }, error -> log.error("Error saving {}: {}", symbol, error.getMessage()));
        });
    }

    public Mono<StockResponseDto> getStockPrice(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quote")
                        .queryParam("symbol", symbol)
                        .queryParam("token", API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    StockResponseDto stockResponseDto = extractLatestPrice(symbol, response);
                    log.info("Symbol: {}, Price: {}", symbol, stockResponseDto.getPrice());
                    return stockResponseDto;
                });
    }

    private StockResponseDto extractLatestPrice(String symbol, String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);

            Double latestPrice = root.path("c").asDouble(0.0); // 현재 가격
            Double startPrice = root.path("o").asDouble(0.0);  // 개장가

            if (latestPrice > 0 && startPrice > 0) {
                double priceDifference = latestPrice - startPrice;
                double priceDifferencePercentage = (priceDifference / startPrice) * 100;

                String priceDifferenceFormatted = String.format("%.2f", priceDifference);
                String priceDifferencePercentageFormatted = String.format("%.2f", priceDifferencePercentage);

                log.info("Start Price: {}, Latest Price: {}, Price difference: {} , Percentage difference: {}%",
                        startPrice, latestPrice, priceDifferenceFormatted, priceDifferencePercentageFormatted);

                return new StockResponseDto(symbol, latestPrice, startPrice,
                        Double.parseDouble(priceDifferenceFormatted), Double.parseDouble(priceDifferencePercentageFormatted));
            }
        } catch (Exception e) {
            log.error("Error parsing Finnhub response for symbol {}: {}", symbol, e.getMessage(), e);
        }
        return new StockResponseDto(symbol, 0.0);
    }

    // 스케줄러용 메서드 (StockLogResponseDto 반환)
    private Mono<StockLogResponseDto> getStockPriceForLog(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quote")
                        .queryParam("symbol", symbol)
                        .queryParam("token", API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> extractLogPrice(symbol, response));
    }

    private StockLogResponseDto extractLogPrice(String symbol, String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);

            Double highPrice = root.path("h").asDouble(0.0);    // 고가
            Double lowPrice = root.path("l").asDouble(0.0);     // 저가
            Double openPrice = root.path("o").asDouble(0.0);    // 개장가
            Double previousClose = root.path("pc").asDouble(0.0); // 전일 종가

            log.info("Symbol: {}, High: {}, Low: {}, Open: {}, Previous Close: {}",
                    symbol, highPrice, lowPrice, openPrice, previousClose);

            return new StockLogResponseDto(symbol, highPrice, lowPrice, openPrice, previousClose, LocalDate.now());
        } catch (Exception e) {
            log.error("Error parsing Finnhub response for symbol {}: {}", symbol, e.getMessage(), e);
            return new StockLogResponseDto(symbol, 0.0, 0.0, 0.0, 0.0, LocalDate.now());
        }
    }

    public List<StockLogResponseDto> getStockHistory(String symbol) {
        LocalDate today = LocalDate.now();

        // symbol로 필터링하고, 오늘 날짜보다 과거 데이터만 조회, 날짜 오름차순 정렬
        List<Stock> stocks = stockRepository.findBySymbolAndDateBeforeOrderByDateAsc(symbol, today);

        return stocks.stream()
                .map(stock -> new StockLogResponseDto(
                        stock.getSymbol(),
                        stock.getHighPrice(),
                        stock.getLowPrice(),
                        stock.getOpenPrice(),
                        stock.getPreviousClose(),
                        stock.getDate()))
                .collect(Collectors.toList());
    }
}

