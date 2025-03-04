package com.dolloer.million.domain.log.service;


import com.dolloer.million.domain.log.dto.response.RevenueHistoryResponseDto;
import com.dolloer.million.domain.log.dto.response.UserAssetResponseDto;
import com.dolloer.million.domain.log.entity.RevenueHistory;
import com.dolloer.million.domain.log.repository.RevenueRepository;
import com.dolloer.million.domain.member.entity.Member;
import com.dolloer.million.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetService {

    private final MemberRepository memberRepository;
    private final RevenueRepository revenueRepository;

    public UserAssetResponseDto getUserAssets() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29);

        List<LocalDate> allDates = IntStream.range(0, 30)
                .mapToObj(i -> endDate.minusDays(i))
                .collect(Collectors.toList());

        List<RevenueHistory> allHistories = revenueRepository.findByDateBetween(startDate, endDate);

        Map<Long, String> memberNameMap = memberRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Member::getId, Member::getName));

        Map<Long, Map<LocalDate, Double>> historyByMember = allHistories.stream()
                .collect(Collectors.groupingBy(
                        rh -> rh.getMember().getId(),
                        Collectors.toMap(RevenueHistory::getDate, RevenueHistory::getTodayTotal, (existing, replacement) -> existing)
                ));

        List<RevenueHistoryResponseDto> assets = new ArrayList<>();
        for (Long memberId : memberNameMap.keySet()) {
            Map<LocalDate, Double> historyMap = historyByMember.getOrDefault(memberId, Collections.emptyMap());
            String memberName = memberNameMap.get(memberId);
            assets.addAll(allDates.stream()
                    .map(date -> new RevenueHistoryResponseDto(date, historyMap.getOrDefault(date, 0.0), memberName))
                    .collect(Collectors.toList()));
        }

        return new UserAssetResponseDto(assets);
    }


}