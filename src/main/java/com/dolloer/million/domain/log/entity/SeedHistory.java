package com.dolloer.million.domain.seed.entity;

import com.dolloer.million.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class SeedHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate date;         // 추가 날짜
    private Double addedSeedMoney;  // 추가 액수
    private Double totalSeedMoney;  // 해당 시점의 시드 총액

    public SeedHistory(Member member, Double addedSeedMoney, Double totalSeedMoney) {
        this.member = member;
        this.date = LocalDate.now();
        this.addedSeedMoney = addedSeedMoney;
        this.totalSeedMoney = totalSeedMoney;
    }

}
