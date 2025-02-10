package com.dolloer.million.domain.member.service;

import com.dolloer.million.aop.Retry;
import com.dolloer.million.domain.member.dto.response.CalculateResponseDto;
import com.dolloer.million.domain.member.dto.response.RevenueResponseDto;
import com.dolloer.million.domain.member.dto.response.UpcomingQuestResponseDto;
import com.dolloer.million.domain.member.entity.Member;
import com.dolloer.million.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final Integer target = 1000000;
    private final Integer dailyGoal =3;

    // 시드 설정
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void retrySetSeedMoney(Long memberId, Double seedMoney) {
        Member member = findMember(memberId);

        if (member.getSeedMoney() == 0) {
            member.initSeed(seedMoney);
        } else {
            member.changeSeed(seedMoney);
        }

        memberRepository.save(member);
    }

    @Retry(maxAttempts = 5)
    public void setSeedMoney(Long memberId, Double seedMoney) {
        retrySetSeedMoney(memberId, seedMoney); // 내부에서 트랜잭션 처리
    }

    // 수익과 저축액 입력
    @Transactional
    public RevenueResponseDto setRevenue(Long memberId, Double dailyRevenue, Double dailySaveMoney) {
        if(dailySaveMoney == null){
            dailySaveMoney=0.0;
        }
        Member member = findMember(memberId);
        double dailyPer = dailyRevenue/member.getTotal()*100;
        if(dailyPer>=(double)dailyGoal/100){
            member.updateSuccessQuest();
        }
        member.updateRevenue(dailyRevenue); // 오늘의 총 수익
        member.updateSaveMoney(dailySaveMoney); // 오늘의 총 저축액
        member.updateUseMoney(dailyRevenue-dailySaveMoney); // 익일 투자 가능 금액
        member.updateTotal();
        memberRepository.save(member);
        return new RevenueResponseDto((dailySaveMoney/dailyRevenue)*100, member.getRevenue(),member.getSaveMoney(),member.getTotal(),member.getSuccessQuest());
    }


    // 시드 대비 총 수익률 계산
    public CalculateResponseDto calculateTotalReturn(Long memberId){
        Member member = findMember(memberId);
        double ret = (member.getTotal()-member.getSeedMoney())/(member.getSeedMoney());

        return new CalculateResponseDto(ret);
    }

    // 처리할 일퀘 시뮬레이터
    public UpcomingQuestResponseDto upcomingQuest(Long memberId, Double per) {
        Member member = findMember(memberId);
        double currentMoney = member.getTotal();

        double growthRate = (1 + dailyGoal / 100) * (1 - per / 100);

        int count = (int) Math.ceil(
                (Math.log(target) - Math.log(currentMoney)) / Math.log(growthRate)
        );

        // 총 저축액 계산
        double totalSavings = 0;
        double tempMoney = currentMoney;
        for (int i = 0; i < count; i++) {
            double dailyInterest = tempMoney * (dailyGoal / 100);
            double dailySavings = dailyInterest * (per / 100);
            totalSavings += dailySavings;
            tempMoney += dailyInterest - dailySavings;
        }

        return new UpcomingQuestResponseDto(tempMoney, totalSavings, count);
    }

    private Member findMember(Long memberId){
        return memberRepository.findByIdWithLock(memberId)
                .orElseThrow(() -> new IllegalArgumentException("등록하고오셈"));
    }
}
