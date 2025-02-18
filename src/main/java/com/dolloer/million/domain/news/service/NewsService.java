package com.dolloer.million.domain.news.service;

import com.dolloer.million.domain.news.dto.response.GoogleSearchResponse;
import com.dolloer.million.domain.news.entity.News;
import com.dolloer.million.domain.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsService {

    @Value("${GOOGLE_API_KEY}")
    private String googleApiKey;

    @Value("${SEARCH_ENGINE_ID}")
    private String searchEngineId;

    @Value("${SEARCH_URL}")
    private String searchUrl;

    private final AtomicInteger searchCount;

    private final WebClient webClient;
    private final NewsRepository newsRepository;

    // 주식 코드 검색
    public List<News> searchStockNews(String ticker) {

        if (searchCount.get() >= 100) {
            log.warn("일일 Google Search API 호출 한도(100회) 초과");
            return Collections.emptyList();
        }

        String query = ticker + " stock news";

        String url = UriComponentsBuilder.fromHttpUrl(searchUrl)
                .queryParam("key", googleApiKey)
                .queryParam("cx", searchEngineId)
                .queryParam("q", query)
                .queryParam("num", 5)
                .queryParam("dateRestrict", "d7")
                .toUriString();

        try{
            log.info("Google Search API 요청 URL: {}", url);
            searchCount.incrementAndGet();

            GoogleSearchResponse response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(GoogleSearchResponse.class)
                    .block();

            List<News> newsList = new ArrayList<>();
            if (response != null && response.getItems() != null) {
                for (GoogleSearchResponse.Item item : response.getItems()) {
                    News news = new News(ticker,item.getTitle(),item.getLink(),item.getSnippet());

                    if (!newsRepository.existsByLink(news.getLink())) {
                        newsRepository.save(news);
                        newsList.add(news);
                    }
                }
            }
            return newsList;
        }
        catch (Exception e){
            log.error("Google Search API 호출 중 에러 발생", e);
            return Collections.emptyList();
        }
    }



}
