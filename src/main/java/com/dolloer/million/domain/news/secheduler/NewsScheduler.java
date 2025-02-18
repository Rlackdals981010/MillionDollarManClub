package com.dolloer.million.domain.news.secheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsScheduler {

    private final AtomicInteger searchCount;

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetSearchCount() {
        searchCount.set(0);
        log.info("Google Search API 호출 횟수 자정 초기화 완료.");
    }
}
