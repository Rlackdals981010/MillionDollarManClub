package com.dolloer.million.domain.news.controller;

import com.dolloer.million.domain.news.entity.News;
import com.dolloer.million.domain.news.service.NewsService;
import com.dolloer.million.response.response.ApiResponse;
import com.dolloer.million.response.response.ApiResponseNewsEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    // 티커 뉴스 검색
    @GetMapping("/{ticker}")
    public ResponseEntity<ApiResponse<List<News>>> searchStockNews(@PathVariable String ticker) {
        List<News> newsList = newsService.searchStockNews(ticker);
        return ResponseEntity.ok(ApiResponse.success(newsList, ApiResponseNewsEnum.NEWS_SEARCH_SUCCESS.getMessage()));
    }

}
