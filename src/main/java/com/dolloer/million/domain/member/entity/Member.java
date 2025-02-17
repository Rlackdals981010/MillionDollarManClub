package com.dolloer.million.domain.money.entity;

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
    private Long version;

    private String name;

    // 시드 머니
    private Double seedMoney;
    // 내 재산
    private Double total;

    private Integer successQuest;

    public Member(String name){
        this.name=name;
        this.seedMoney = 0.0;
        this.total = 0.0;
        this.successQuest=0;
    }

    // 시드 초기화
    public void initSeed(Double seedMoney){
        this.seedMoney = seedMoney;
        this.total = this.seedMoney;
    }
    // 시드 추가
    public void changeSeed(Double seedMoney){
        this.seedMoney += seedMoney;
        this.total += seedMoney;
    }

    // 일퀘 성공시 증가
    public void updateSuccessQuest(){
        this.successQuest++;
    }

    // 자산 업데이트
    public void updateTotal(Double newTotal) {
        this.total = newTotal;
    }

}
