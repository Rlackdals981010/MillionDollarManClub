package com.dolloer.million.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long seedMoney;
    private Long nowMoney;
    private Integer revenue;

    public User (String name){
        this.name=name;
        this.seedMoney = 0L;
        this.nowMoney = 0L;
        this.revenue = 0;
    }

    public void initSeed(Long seedMoney){
        this.seedMoney = seedMoney;
        this.nowMoney = this.seedMoney;
    }

}
