package com.dolloer.million.domain.log.service;


import com.dolloer.million.domain.log.dto.response.RevenueResponseDto;
import com.dolloer.million.domain.log.dto.response.SeedResponseDto;
import com.dolloer.million.domain.log.entity.RevenueHistory;
import com.dolloer.million.domain.log.entity.SeedHistory;
import com.dolloer.million.domain.log.repository.RevenueRepository;
import com.dolloer.million.domain.log.repository.SeedRepository;
import com.dolloer.million.domain.member.entity.Member;
import com.dolloer.million.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {

    private final Integer dailyGoal =3;

    private final MemberRepository memberRepository;
    private final SeedRepository seedRepository;
    private final RevenueRepository revenueRepository;

    // 시드 설정
    @Transactional
    public void setSeedMoney(Long memberId, Double seedMoney) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("등록하고오셈"));
        try{
            if (member.getSeedMoney() == 0) {
                member.initSeed(seedMoney);
            } else {
                member.changeSeed(seedMoney);
            }
            SeedHistory seedHistory = new SeedHistory(member, seedMoney, member.getSeedMoney());
            seedRepository.save(seedHistory);

            memberRepository.saveAndFlush(member);
        }catch (ObjectOptimisticLockingFailureException e){
            log.info("Method: setSeedMoney, Version:{}, ID:{}, seedMoney:{} 동시성 문제 발생", member.getVersion(), memberId, seedMoney);
        }
    }

    // 시드 로그 반환
    @Transactional(readOnly = true)
    public Page<SeedResponseDto> getSeedHistory(Long memberId, Pageable pageable) {
        Page<SeedHistory> seedHistories = seedRepository.findByMemberId(memberId, pageable);
        return seedHistories.map(SeedResponseDto::from);
    }

    // 수익 설정
    @Transactional
    public void setRevenue(Long memberId, Double dailyRevenue) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("등록하고오셈"));

        LocalDate currentDate = LocalDate.now();

        try {
            RevenueHistory existingHistory = revenueRepository.findByMemberIdAndDate(memberId, currentDate)
                    .orElse(null);

            if (existingHistory != null) {
                // 기존 데이터 업데이트
                double newRevenue = existingHistory.getAddedRevenueMoney() + dailyRevenue;
                double newSaveMoney = existingHistory.getAddedSaveMoney();
                double newTotal = member.getTotal() + dailyRevenue;
                double newPercent = (double) Math.round((newRevenue / newTotal) * 100 * 100) / 100;
                boolean quest = newPercent >= dailyGoal;

                existingHistory.update(newRevenue, newSaveMoney, newPercent, newTotal, quest);
                member.updateTotal(newTotal);
                revenueRepository.save(existingHistory);
            } else {
                // 새 데이터 생성
                boolean quest = false;
                double dailyPer = (double) Math.round((dailyRevenue / member.getTotal()) * 100 * 100) / 100;
                if (dailyPer >= dailyGoal) {
                    member.updateSuccessQuest();
                    quest = true;
                }

                RevenueHistory revenueHistory = new RevenueHistory(
                        member, dailyRevenue, 0.0, dailyPer,
                        member.getTotal() + dailyRevenue, quest);
                member.updateTotal(member.getTotal() + dailyRevenue);
                revenueRepository.save(revenueHistory);
            }

            memberRepository.saveAndFlush(member);

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new RuntimeException(
                    String.format("Method: setRevenue, Version:%d, ID:%d, dailyRevenue:%.2f, 동시성 문제 발생. 원인: %s",
                            member.getVersion(), memberId, dailyRevenue, e.getMessage())
            );
        }
    }

    @Transactional
    public void setSaveMoney(Long memberId, Double dailySaveMoney) {
        if (dailySaveMoney == null) {
            dailySaveMoney = 0.0;
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("등록하고오셈"));

        LocalDate currentDate = LocalDate.now();

        try {
            RevenueHistory existingHistory = revenueRepository.findByMemberIdAndDate(memberId, currentDate)
                    .orElse(null);

            if (existingHistory != null) {
                // 기존 데이터 업데이트
                double newRevenue = existingHistory.getAddedRevenueMoney();
                double newSaveMoney = existingHistory.getAddedSaveMoney() + dailySaveMoney;
                double newTotal = existingHistory.getTodayTotal();
                double newPercent = (double) Math.round((newRevenue / newTotal) * 100 * 100) / 100;
                boolean quest = existingHistory.getQuest();

                existingHistory.update(newRevenue, newSaveMoney, newPercent, newTotal, quest);
                member.updateSaveMoney(member.getSaveMoney() + dailySaveMoney);
                revenueRepository.save(existingHistory);
            } else {
                // 새 데이터 생성
                RevenueHistory revenueHistory = new RevenueHistory(
                        member, 0.0, dailySaveMoney, 0.0,
                        member.getTotal(), false);
                member.updateSaveMoney(member.getSaveMoney() + dailySaveMoney);
                revenueRepository.save(revenueHistory);
            }

            memberRepository.saveAndFlush(member);

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new RuntimeException(
                    String.format("Method: setSaveMoney, Version:%d, ID:%d, dailySaveMoney:%.2f, 동시성 문제 발생. 원인: %s",
                            member.getVersion(), memberId, dailySaveMoney, e.getMessage())
            );
        }
    }

    // 수익 로그 반환
    @Transactional(readOnly = true)
    public Page<RevenueResponseDto> getRevenueHistory(Long memberId, Pageable pageable) {
        Page<RevenueHistory> revenueHistories = revenueRepository.findByMemberId(memberId, pageable);
        return revenueHistories.map(RevenueResponseDto::from);
    }
}
