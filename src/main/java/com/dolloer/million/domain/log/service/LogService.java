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

    // 시드 설정
    @Transactional
    public void setSeedMoney(Long memberId, Double seedMoney) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("등록하고오셈"));
        try {
            LocalDate today = LocalDate.now();

            // 오늘의 SeedHistory 기록 조회
            Optional<SeedHistory> existingHistory = seedRepository.findByMemberAndDate(member, today);

            if (member.getSeedMoney() == 0) {
                member.initSeed(seedMoney);
            } else {
                member.changeSeed(seedMoney);
            }

            if (existingHistory.isPresent()) {
                // 기존 기록이 있으면 금액 합산
                SeedHistory history = existingHistory.get();
                history.addAmount(seedMoney);
                seedRepository.save(history);
            } else {
                // 기록이 없으면 새로 저장
                SeedHistory seedHistory = new SeedHistory(member, seedMoney, member.getSeedMoney());
                seedRepository.save(seedHistory);
            }

            memberRepository.saveAndFlush(member);
        } catch (ObjectOptimisticLockingFailureException e) {
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
    public void setRevenue(Long memberId, Double dailyRevenue, String date) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("등록하고오셈"));

        LocalDate targetDate = date != null
                ? LocalDate.parse(date) // 클라이언트에서 받은 날짜 사용
                : LocalDate.now(); // 날짜가 없으면 기본값으로 오늘 사용

        try {
            // targetDate 이전의 가장 최근 RevenueHistory 찾기
            RevenueHistory latestHistory = revenueRepository.findTopByMemberIdAndDateBeforeOrderByDateDesc(memberId, targetDate)
                    .orElse(null);

            // 기준 total 계산: RevenueHistory가 없으면 SeedHistory에서 가져옴
            double baseTotal;
            if (latestHistory != null) {
                baseTotal = latestHistory.getTodayTotal();
            } else {
                SeedHistory latestSeed = seedRepository.findTopByMemberIdAndDateBeforeOrderByDateDesc(memberId, targetDate)
                        .orElse(null);
                baseTotal = (latestSeed != null) ? latestSeed.getTotalSeedMoney() : member.getSeedMoney(); // SeedHistory도 없으면 그냥 최신 기준 시드 머니
            }

            // targetDate의 데이터 처리
            RevenueHistory existingHistory = revenueRepository.findByMemberIdAndDate(memberId, targetDate)
                    .orElse(null);

            double newTotal; // newTotal 변수를 여기서 선언
            if (existingHistory != null) {
                // 기존 데이터 업데이트
                double newRevenue = existingHistory.getAddedRevenueMoney() + dailyRevenue;
                double newSaveMoney = existingHistory.getAddedSaveMoney();
                newTotal = baseTotal + newRevenue + newSaveMoney;
                double newPercent = baseTotal > 0 ? (double) Math.round((newRevenue / baseTotal) * 100 * 100) / 100 : 0.0;
                boolean quest = newPercent >= dailyGoal;

                existingHistory.update(newRevenue, newSaveMoney, newPercent, newTotal, quest);
                revenueRepository.save(existingHistory);
            } else {
                // 새 데이터 생성
                double newRevenue = dailyRevenue;
                double newSaveMoney = 0.0;
                newTotal = baseTotal + dailyRevenue;
                double newPercent = baseTotal > 0 ? (double) Math.round((dailyRevenue / baseTotal) * 100 * 100) / 100 : 0.0;
                boolean quest = newPercent >= dailyGoal;

                if (quest) {
                    member.updateSuccessQuest();
                }

                RevenueHistory revenueHistory = new RevenueHistory(
                        member, targetDate, newRevenue, newSaveMoney, newPercent, newTotal, quest);
                revenueRepository.save(revenueHistory);
            }

            // targetDate가 오늘 날짜와 같으면 Member의 total 업데이트
            if (targetDate.equals(LocalDate.now())) {
                member.updateTotal(newTotal);
            }

            // targetDate 이후의 모든 데이터 업데이트
            updateSubsequentTotals(memberId, targetDate);

            memberRepository.saveAndFlush(member);

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new RuntimeException(
                    String.format("Method: setRevenue, Version:%d, ID:%d, dailyRevenue:%.2f, date:%s, 동시성 문제 발생. 원인: %s",
                            member.getVersion(), memberId, dailyRevenue, targetDate.toString(), e.getMessage())
            );
        }
    }

    @Transactional
    public void setSaveMoney(Long memberId, Double dailySaveMoney, String date) {
        if (dailySaveMoney == null) {
            dailySaveMoney = 0.0;
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("등록하고오셈"));

        LocalDate targetDate = date != null
                ? LocalDate.parse(date) // 클라이언트에서 받은 날짜 사용
                : LocalDate.now(); // 날짜가 없으면 기본값으로 오늘 사용

        try {
            // targetDate 이전의 가장 최근 RevenueHistory 찾기
            RevenueHistory latestHistory = revenueRepository.findTopByMemberIdAndDateBeforeOrderByDateDesc(memberId, targetDate)
                    .orElse(null);

            // 기준 total 계산: RevenueHistory가 없으면 SeedHistory에서 가져옴
            double baseTotal;
            if (latestHistory != null) {
                baseTotal = latestHistory.getTodayTotal();
            } else {
                SeedHistory latestSeed = seedRepository.findTopByMemberIdAndDateBeforeOrderByDateDesc(memberId, targetDate)
                        .orElse(null);
                baseTotal = (latestSeed != null) ? latestSeed.getTotalSeedMoney() : 0.0; // SeedHistory도 없으면 0
            }

            // targetDate의 데이터 처리
            RevenueHistory existingHistory = revenueRepository.findByMemberIdAndDate(memberId, targetDate)
                    .orElse(null);

            if (existingHistory != null) {
                // 기존 데이터 업데이트
                double newRevenue = existingHistory.getAddedRevenueMoney();
                double newSaveMoney = existingHistory.getAddedSaveMoney() + dailySaveMoney;
                double newTotal = baseTotal + newRevenue + newSaveMoney;
                double newPercent = newTotal > 0 ? (double) Math.round((newRevenue / newTotal) * 100 * 100) / 100 : 0.0;
                boolean quest = existingHistory.getQuest(); // 기존 퀘스트 유지

                existingHistory.update(newRevenue, newSaveMoney, newPercent, newTotal, quest);
                revenueRepository.save(existingHistory);
            } else {
                // 새 데이터 생성
                double newRevenue = 0.0;
                double newSaveMoney = dailySaveMoney;
                double newTotal = baseTotal + dailySaveMoney;
                double newPercent = 0.0;
                boolean quest = false;

                RevenueHistory revenueHistory = new RevenueHistory(
                        member, targetDate, newRevenue, newSaveMoney, newPercent, newTotal, quest);
                revenueRepository.save(revenueHistory);
            }

            // targetDate 이후의 모든 데이터 업데이트
            updateSubsequentTotals(memberId, targetDate);

            memberRepository.saveAndFlush(member);

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new RuntimeException(
                    String.format("Method: setSaveMoney, Version:%d, ID:%d, dailySaveMoney:%.2f, date:%s, 동시성 문제 발생. 원인: %s",
                            member.getVersion(), memberId, dailySaveMoney, targetDate.toString(), e.getMessage())
            );
        }
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
