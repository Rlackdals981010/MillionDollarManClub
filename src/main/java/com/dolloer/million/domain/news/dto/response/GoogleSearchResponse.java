package com.dolloer.million.domain.news.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class GoogleSearchResponse {

    private List<Item> items;  // API 응답에서 'items' 필드를 나타냄

    @Data
    public static class Item {
        private String title;    // 뉴스 제목
        private String link;     // 뉴스 링크
        private String snippet;  // 뉴스 내용의 짧은 설명
    }
}