package com.dolloer.million.domain.news.service;


import com.dolloer.million.domain.news.dto.response.NewsSentimentResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class NewsService {

    @Value("${GOOGLE_API_KEY}")
    private String googleApiKey;

    @Value("${SEARCH_ENGINE_ID}")
    private String searchEngineId;

    @Value("${SEARCH_URL}")
    private String searchUrl;

    @Value("${GEMINI_API_KEY}")
    private String geminiApiKey;

    private final AtomicInteger searchCount = new AtomicInteger(0);

    private final WebClient webClient;

    public NewsService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(searchUrl).build();
    }

    public Mono<List<NewsSentimentResponseDto>> searchAndAnalyzeStockNews(String ticker) {
        if (searchCount.get() >= 100) {
            log.warn("일일 Google Search API 호출 한도(100회) 초과: ticker = {}", ticker);
            return Mono.just(Collections.emptyList());
        }

        String query = ticker + " stock market news";
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7); // 7일 이내 최신 뉴스

        String url = UriComponentsBuilder.fromHttpUrl(searchUrl)
                .queryParam("key", googleApiKey)
                .queryParam("cx", searchEngineId)
                .queryParam("q", query)
                .queryParam("num", 5) // 최신순 5개
                .queryParam("dateRestrict", "d7")
                .queryParam("sort", "date:r") // 최신순 정렬
                .queryParam("siteSearch", "www.reuters.com,www.bloomberg.com,www.nasdaq.com,www.investing.com")

                .toUriString();

        log.info("Google Search API 요청 URL: {}", url);
        searchCount.incrementAndGet();

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
                        .defaultIfEmpty("Client Error: " + response.statusCode())
                        .flatMap(error -> Mono.error(new RuntimeException(error))))
                .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
                        .defaultIfEmpty("Server Error: " + response.statusCode())
                        .flatMap(error -> Mono.error(new RuntimeException(error))))
                .bodyToMono(String.class)
                .flatMap(response -> processGoogleSearchResponse(response, ticker));
    }

    private Mono<List<NewsSentimentResponseDto>> processGoogleSearchResponse(String response, String ticker) {
        log.info("Google Search API 응답: {}", response);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.path("items");

            Set<String> uniqueLinks = new HashSet<>(); // 메모리 내 중복 링크 관리
            List<Mono<NewsSentimentResponseDto>> sentimentFluxes = new ArrayList<>();

            items.forEach(item -> {
                String link = item.path("link").asText();
                if (uniqueLinks.size() < 5 && uniqueLinks.add(link)) { // 최대 5개, 중복 제거
                    String title = item.path("title").asText().replace(")", "").replace("(", "");
                    // 스니펫은 제외하고 제목만 사용
                    log.debug("Processed news title: {}", title);
                    // Gemini API로 감정 분석 (제목만 전달)
                    sentimentFluxes.add(analyzeNewsSentiment(title, ticker));
                }
            });

            return Flux.merge(sentimentFluxes)
                    .collectList()
                    .map(this::calculateAverageSentiment);
        } catch (Exception e) {
            log.error("Google Search API 파싱 중 에러 발생: ticker = {}, error = {}", ticker, e.getMessage(), e);
            return Mono.just(Collections.emptyList());
        }
    }

    private List<NewsSentimentResponseDto> calculateAverageSentiment(List<NewsSentimentResponseDto> sentiments) {
        if (sentiments.isEmpty() || sentiments.size() != 5) { // 5개 뉴스 확인
            log.warn("감정 분석 결과가 5개가 아닙니다: size = {}", sentiments.size());
            return Collections.emptyList();
        }

        // 최신 5개 뉴스의 긍정/부정 퍼센트 평균 계산
        double totalPositive = 0.0;
        double totalNegative = 0.0;
        for (NewsSentimentResponseDto sentiment : sentiments) {
            totalPositive += Double.parseDouble(sentiment.getPositivePercentage());
            totalNegative += Double.parseDouble(sentiment.getNegativePercentage());
        }

        double avgPositive = totalPositive / 5; // 항상 5개로 고정
        double avgNegative = totalNegative / 5; // 항상 5개로 고정

        // 최종 결과: ticker와 평균 긍정/부정 퍼센트 (소수점 2자리)
        return List.of(new NewsSentimentResponseDto(
                sentiments.get(0).getTicker(), // 첫 번째 ticker 사용
                String.format("%.2f", avgPositive),
                String.format("%.2f", avgNegative)
        ));
    }

    // Gemini API로 뉴스 감정 분석 (비동기)
    private Mono<NewsSentimentResponseDto> analyzeNewsSentiment(String newsText, String ticker) {
        // 특수 문자를 이스케이프하거나 제거 (예: ) 제거)
        String sanitizedNewsText = newsText.replace(")", "").replace("(", "");
        String prompt = String.format(
                "다음 주식 뉴스 텍스트를 분석해 해당 주식이 긍정적인지 부정적인지 판단하고, 긍정/부정 비율 0~100로 몇대 몇인지 응답해 주세요: %s", sanitizedNewsText);

        return WebClient.create("https://generativelanguage.googleapis.com")
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-1.5-flash:generateContent")
                        .queryParam("key", geminiApiKey)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"contents\": [{\"role\": \"user\", \"parts\": [{\"text\": \"" + prompt + "\"}]}]}")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
                        .defaultIfEmpty("Client Error: " + response.statusCode())
                        .flatMap(error -> Mono.error(new RuntimeException(error))))
                .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
                        .defaultIfEmpty("Server Error: " + response.statusCode())
                        .flatMap(error -> Mono.error(new RuntimeException(error))))
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode root = objectMapper.readTree(response);
                        String sentiment = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
                        return parseSentiment(sentiment, ticker);
                    } catch (Exception e) {
                        log.error("Gemini API 감정 분석 중 에러 발생: newsText = {}, ticker = {}, error = {}", sanitizedNewsText, ticker, e.getMessage(), e);
                        return new NewsSentimentResponseDto(ticker, "0", "100"); // 기본값: 부정 100%
                    }
                });
    }

    private NewsSentimentResponseDto parseSentiment(String sentimentText, String ticker) {
        String[] parts = sentimentText.split("%");
        if (parts.length >= 2) {
            String positive = parts[0].replace("긍정 ", "").trim();
            String negative = parts[1].replace("부정 ", "").trim();
            return new NewsSentimentResponseDto(ticker, positive, negative);
        }
        return new NewsSentimentResponseDto(ticker, "0", "100"); // 기본값: 부정 100%
    }
}
