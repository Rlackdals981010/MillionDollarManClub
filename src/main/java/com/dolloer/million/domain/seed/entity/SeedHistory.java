package com.dolloer.million.domain.member.entity;

import com.dolloer.million.domain.money.entity.Member;
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

    private LocalDate date;
    private Double addedSeedMoney;

    public SeedHistory(Member member, Double addedSeedMoney) {
        this.member = member;
        this.date = LocalDate.now();
        this.addedSeedMoney = addedSeedMoney;
    }
}
