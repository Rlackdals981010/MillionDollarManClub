package com.dolloer.million.domain.news.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ticker;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String link;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public News(String ticker,String title,String link,String summary ){
        this.ticker = ticker;
        this.title = title;
        this.link = link;
        this.summary = summary;
    }

}
