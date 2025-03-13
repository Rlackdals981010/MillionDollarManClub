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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {

    private final Integer dailyGoal =3;

    private final MemberRepository memberRepository;
    private final SeedRepository seedRepository;
    private final RevenueRepository revenueRepository;

    @Transactional
    public void setSeedMoney(Long memberId, Double seedMoney) {
        Member member = getMember(memberId);
        LocalDate today = LocalDate.now();
        double baseTotal = calculateBaseTotal(memberId, today, member);

        try {
            Optional<SeedHistory> existingHistory = seedRepository.findByMemberAndDate(member, today);

            if (member.getSeedMoney() == 0) {
                member.initSeed(seedMoney);
            } else {
                member.changeSeed(seedMoney);
            }

            if (existingHistory.isPresent()) {
                SeedHistory history = existingHistory.get();
                history.addAmount(seedMoney);
                seedRepository.save(history);
            } else {
                SeedHistory seedHistory = new SeedHistory(member, seedMoney, member.getSeedMoney());
                seedRepository.save(seedHistory);
            }

            RevenueHistory todayHistory = revenueRepository.findByMemberIdAndDate(memberId, today)
                    .orElse(new RevenueHistory(member, today, 0.0, 0.0, 0.0, baseTotal, false));

            double newTotal = baseTotal + seedMoney;
            todayHistory.update(todayHistory.getAddedRevenueMoney(), todayHistory.getAddedSaveMoney(),
                    todayHistory.getAddedRevenuePercent(), newTotal, todayHistory.getQuest());

            revenueRepository.save(todayHistory);

            updateSubsequentTotals(memberId, today);
            memberRepository.saveAndFlush(member);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new RuntimeException(
                    String.format("Method: setSeedMoney, Version:%d, ID:%d, seedMoney:%.2f, 동시성 문제 발생. 원인: %s",
                            member.getVersion(), memberId, seedMoney, e.getMessage())
            );
        }
    }

    @Transactional(readOnly = true)
    public Page<SeedResponseDto> getSeedHistory(Long memberId, Pageable pageable) {
        Page<SeedHistory> seedHistories = seedRepository.findByMemberId(memberId, pageable);
        return seedHistories.map(SeedResponseDto::from);
    }


    // 수익 설정
    @Transactional
    public void setRevenue(Long memberId, Double dailyRevenue, String date) {
        Member member = getMember(memberId);
        LocalDate targetDate = parseDate(date);
        double baseTotal = calculateBaseTotal(memberId, targetDate, member);

        try {
            RevenueHistory existingHistory = revenueRepository.findByMemberIdAndDate(memberId, targetDate).orElse(null);
            double newTotal;

            if (existingHistory != null) {
                double newRevenue = existingHistory.getAddedRevenueMoney() + dailyRevenue;
                double newSaveMoney = existingHistory.getAddedSaveMoney();
                newTotal = baseTotal + newRevenue + newSaveMoney;
                double newPercent = baseTotal > 0 ? Math.round((newRevenue / baseTotal) * 100 * 100) / 100.0 : 0.0;
                boolean quest = newPercent >= dailyGoal;

                existingHistory.update(newRevenue, newSaveMoney, newPercent, newTotal, quest);
                revenueRepository.save(existingHistory);
            } else {
                double newRevenue = dailyRevenue;
                double newSaveMoney = 0.0;
                newTotal = baseTotal + dailyRevenue;
                double newPercent = baseTotal > 0 ? Math.round((dailyRevenue / baseTotal) * 100 * 100) / 100.0 : 0.0;
                boolean quest = newPercent >= dailyGoal;

                if (quest) {
                    member.updateSuccessQuest();
                }

                RevenueHistory revenueHistory = new RevenueHistory(
                        member, targetDate, newRevenue, newSaveMoney, newPercent, newTotal, quest);
                revenueRepository.save(revenueHistory);
            }

            if (targetDate.equals(LocalDate.now())) {
                member.updateTotal(newTotal);
            }

            updateSubsequentTotals(memberId, targetDate);
            memberRepository.saveAndFlush(member);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleConcurrencyException("setRevenue", member, memberId, dailyRevenue, targetDate, e);
        }
    }

    @Transactional
    public void setSaveMoney(Long memberId, Double dailySaveMoney, String date) {
        Member member = getMember(memberId);
        LocalDate targetDate = parseDate(date);
        double baseTotal = calculateBaseTotal(memberId, targetDate, member);

        try {
            RevenueHistory existingHistory = revenueRepository.findByMemberIdAndDate(memberId, targetDate).orElse(null);
            double newTotal;

            if (existingHistory != null) {
                double newRevenue = existingHistory.getAddedRevenueMoney();
                double newSaveMoney = existingHistory.getAddedSaveMoney() + dailySaveMoney;
                newTotal = baseTotal + newRevenue + newSaveMoney;
                double newPercent = newTotal > 0 ? Math.round((newRevenue / newTotal) * 100 * 100) / 100.0 : 0.0;
                boolean quest = existingHistory.getQuest();

                existingHistory.update(newRevenue, newSaveMoney, newPercent, newTotal, quest);
                revenueRepository.save(existingHistory);
            } else {
                double newRevenue = 0.0;
                double newSaveMoney = dailySaveMoney;
                newTotal = baseTotal + dailySaveMoney;
                double newPercent = 0.0;
                boolean quest = false;

                RevenueHistory revenueHistory = new RevenueHistory(
                        member, targetDate, newRevenue, newSaveMoney, newPercent, newTotal, quest);
                revenueRepository.save(revenueHistory);
            }

            updateSubsequentTotals(memberId, targetDate);
            memberRepository.saveAndFlush(member);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleConcurrencyException("setSaveMoney", member, memberId, dailySaveMoney, targetDate, e);
        }
    }

    private void handleConcurrencyException(String methodName, Member member, Long memberId, Double value, LocalDate targetDate, ObjectOptimisticLockingFailureException e) {
        throw new RuntimeException(
                String.format("Method: %s, Version:%d, ID:%d, Value:%.2f, date:%s, 동시성 문제 발생. 원인: %s",
                        methodName, member.getVersion(), memberId, value, targetDate.toString(), e.getMessage())
        );
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("등록하고오셈"));
    }

    private LocalDate parseDate(String date) {
        return (date != null) ? LocalDate.parse(date) : LocalDate.now();
    }

    private double calculateBaseTotal(Long memberId, LocalDate targetDate, Member member) {
        RevenueHistory latestHistory = revenueRepository.findTopByMemberIdAndDateBeforeOrderByDateDesc(memberId, targetDate)
                .orElse(null);

        if (latestHistory != null) {
            return latestHistory.getTodayTotal();
        }

        SeedHistory latestSeed = seedRepository.findTopByMemberIdAndDateBeforeOrderByDateDesc(memberId, targetDate)
                .orElse(null);

        return (latestSeed != null) ? latestSeed.getTotalSeedMoney() : member.getSeedMoney();
    }


    // 수익 로그 반환
    @Transactional(readOnly = true)
    public Page<RevenueResponseDto> getRevenueHistory(Long memberId, Pageable pageable) {
        Page<RevenueHistory> revenueHistories = revenueRepository.findByMemberId(memberId, pageable);
        return revenueHistories.map(RevenueResponseDto::from);
    }

    private void updateSubsequentTotals(Long memberId, LocalDate targetDate) {
        // targetDate 이후의 모든 RevenueHistory를 날짜순으로 가져옴
        List<RevenueHistory> subsequentHistories = revenueRepository.findByMemberIdAndDateAfterOrderByDateAsc(memberId, targetDate);

        if (subsequentHistories.isEmpty()) {
            return; // 이후 데이터가 없으면 종료
        }
        // targetDate의 todayTotal을 기준으로 시작
        RevenueHistory targetHistory = revenueRepository.findByMemberIdAndDate(memberId, targetDate)
                .orElseThrow(() -> new IllegalStateException("targetDate 데이터가 존재해야 함"));
        double previousTotal = targetHistory.getTodayTotal();

        // 이후 레코드 순차적으로 업데이트
        for (RevenueHistory history : subsequentHistories) {
            double newRevenue = history.getAddedRevenueMoney();
            double newSaveMoney = history.getAddedSaveMoney();
            double newTotal = previousTotal + newRevenue + newSaveMoney;
            double newPercent = newTotal > 0 ? (double) Math.round((newRevenue / newTotal) * 100 * 100) / 100 : 0.0;
            boolean quest = newPercent >= dailyGoal;

            history.update(newRevenue, newSaveMoney, newPercent, newTotal, quest);
            revenueRepository.save(history);

            // 다음 반복을 위해 previousTotal 갱신
            previousTotal = newTotal;
        }
    }
}
