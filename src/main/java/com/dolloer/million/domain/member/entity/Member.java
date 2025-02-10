package com.dolloer.million.domain.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Integer version;

    private String name;

    // 시드 머니
    private Double seedMoney;
    // 내 재산
    private Double total;
    // 누적 번 돈
    private Double revenue;
    // 저축된 돈
    private Double saveMoney;
    // 투자가능금액 revenue-saveMoney
    private Double useMoney;
    // 성공한 일퀘
    private Integer successQuest;

    public Member(String name){
        this.name=name;
        this.seedMoney = 0.0;
        this.total = 0.0;
        this.revenue = 0.0;
        this.saveMoney = 0.0;
        this.useMoney=0.0;
        this.successQuest=0;
    }

    // 시드 초기화
    public void initSeed(Double seedMoney){
        this.seedMoney = seedMoney;
        this.useMoney = this.seedMoney;
        this.total = this.seedMoney;
    }
    // 시드 추가
    public void changeSeed(Double seedMoney){
        this.seedMoney += seedMoney;
        this.useMoney += seedMoney;
        this.total += seedMoney;
    }

    // 수익 갱신
    public void updateRevenue(Double revenue){
        this.revenue +=revenue;
    }

    // 저축액 갱신
    public void updateSaveMoney(Double saveMoney){
        this.saveMoney += saveMoney;
    }

    // 투자가능금액 갱신
    public void updateUseMoney(Double useMoney){
        this.useMoney += useMoney;
    }

    // 일퀘 성공시 증가
    public void updateSuccessQuest(){
        this.successQuest++;
    }

    // 재산 업데이트
    public void updateTotal(){
        this.total = this.useMoney + this.saveMoney;
    }

}
