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
    public void setRevenueMoney(Long memberId, Double dailyRevenue, Double dailySaveMoney){
        if(dailySaveMoney == null){
            dailySaveMoney=0.0;
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("등록하고오셈"));

        try{
            boolean quest = false;
            double dailyPer = (double) Math.round((dailyRevenue / member.getTotal()) * 100 * 100) / 100;
            if(dailyPer>=dailyGoal){
                member.updateSuccessQuest();
                quest=true;
            }
            RevenueHistory revenueHistory = new RevenueHistory(member, dailyRevenue, dailySaveMoney, dailyPer,member.getTotal()+dailyRevenue,quest);
            member.updateTotal(member.getTotal() + dailyRevenue);
            member.updateSaveMoney(dailySaveMoney);
            revenueRepository.save(revenueHistory);

        }catch (ObjectOptimisticLockingFailureException e) {
            throw new RuntimeException(
                    String.format("Method: setRevenue, Version:%d, ID:%d, dailyRevenue:%.2f, dailySaveMoney:%.2f  동시성 문제 발생. 원인: %s",
                            member.getVersion(), memberId, dailyRevenue, dailySaveMoney, e.getMessage())
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
