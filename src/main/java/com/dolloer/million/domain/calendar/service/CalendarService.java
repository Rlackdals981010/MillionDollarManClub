package com.dolloer.million.domain.calendar.service;

import com.dolloer.million.domain.calendar.dto.response.DailyQuestResponseDto;
import com.dolloer.million.domain.log.entity.RevenueHistory;
import com.dolloer.million.domain.log.repository.RevenueRepository;
import com.dolloer.million.domain.member.entity.Member;
import com.dolloer.million.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final MemberRepository memberRepository;
    private final RevenueRepository revenueRepository;



    public List<DailyQuestResponseDto> getCalendar(Long memberId, YearMonth month) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("등록하고오셈"));

        List<RevenueHistory> histories = revenueRepository.findByMemberAndDateBetween(
                member,
                month.atDay(1),
                month.atEndOfMonth()
        );

        return histories.stream()
                .map(history -> DailyQuestResponseDto.from(history,month))
                .collect(Collectors.toList());
    }
}
