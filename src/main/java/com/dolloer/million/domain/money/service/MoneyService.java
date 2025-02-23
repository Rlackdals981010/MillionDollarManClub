package com.dolloer.million.domain.money.service;

import com.dolloer.million.domain.member.entity.Member;
import com.dolloer.million.domain.member.repository.MemberRepository;
import com.dolloer.million.domain.money.dto.response.UpcomingQuestResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MoneyService {

    private final MemberRepository memberRepository;
    private final Integer target = 1000000; // 100만불
    private final Double dailyGoal =1.03;  // 3퍼 수익


    // 저축 비율 입력시 처리할 일퀘 시뮬레이터
    public UpcomingQuestResponseDto upcomingQuest(Long memberId, Double per) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("등록하고오셈"));

        double currentMoney = member.getTotal();

        // 3% 성장 후 per% 만큼 저축
        double growthRate = 1 + ((dailyGoal - 1) * (1 - per / 100));

        // 성장률이 1 이하라면 목표에 도달할 수 없으므로 예외 처리
        if (growthRate <= 1) {
            throw new IllegalArgumentException("저축 비율이 너무 커서 목표를 달성할 수 없습니다.");
        }

        // 필요한 일수 계산
        int count = (int) Math.ceil(
                (Math.log(target) - Math.log(currentMoney)) / Math.log(growthRate)
        );

        return new UpcomingQuestResponseDto(count);
    }



}
