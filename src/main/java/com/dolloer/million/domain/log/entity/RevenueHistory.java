package com.dolloer.million.domain.log.entity;

import com.dolloer.million.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class RevenueHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate date;             // 추가 날짜
    private Double addedRevenueMoney;   // 당일 수익
    private Double addedSaveMoney;      // 당일 저축 액
    private Double addedRevenuePercent; // 당일 수익률
    private Double todayTotal;          // 당일 총액

    public RevenueHistory(Member member, Double addedRevenueMoney, Double addedSaveMoney, Double addedRevenuePercent, Double todayTotal){
        this.member = member;
        this.date = LocalDate.now();
        this.addedRevenueMoney = addedRevenueMoney;
        this.addedSaveMoney = addedSaveMoney;
        this.addedRevenuePercent = addedRevenuePercent;
        this.todayTotal =todayTotal;
    }
}
