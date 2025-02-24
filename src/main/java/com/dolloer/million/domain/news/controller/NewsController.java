package com.dolloer.million.domain.news.controller;

import com.dolloer.million.domain.news.dto.response.NewsSentimentResponseDto;
import com.dolloer.million.domain.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NewsController {

    private final NewsService newsService;

    // 티커 뉴스 검색
    @GetMapping("/news")
    public Mono<ResponseEntity<List<NewsSentimentResponseDto>>> getStockNewsAndSentiment(@RequestParam String ticker) {
        Mono<ResponseEntity<List<NewsSentimentResponseDto>>> ret =newsService.searchAndAnalyzeStockNews(ticker)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
        return ret;
    }

}
