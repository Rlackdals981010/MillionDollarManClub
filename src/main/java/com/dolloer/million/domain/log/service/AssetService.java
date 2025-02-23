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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetService {

    private final MemberRepository memberRepository;
    private final RevenueRepository revenueRepository;

    public UserAssetResponseDto getUserAssets(Long memberId) {
        List<RevenueHistoryResponseDto> assets = new ArrayList<>();

        // 현재 사용자 데이터 추가 (isCurrentUser = true, 리스트 첫 번째로 위치)
        assets.addAll(getMemberAssetData(memberId, true));

        // 데이터베이스에 있는 모든 유저 데이터 추가 (현재 사용자 제외, isCurrentUser = false)
        List<Member> allMembers = memberRepository.findAll();
        for (Member member : allMembers) {
            Long otherMemberId = member.getId();
            log.info(String.valueOf(otherMemberId));
            if (!otherMemberId.equals(memberId)) { // 현재 사용자는 중복 제외
                assets.addAll(getMemberAssetData(otherMemberId, false));
            }
        }

        log.info("모든 유저 자산 데이터 조회 완료: 현재 사용자 ID = {}", memberId);
        return new UserAssetResponseDto(assets);
    }

    private List<RevenueHistoryResponseDto> getMemberAssetData(Long memberId, boolean isCurrentUser) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29); // 30일 범위 시작 (endDate 포함)

        // 30일 범위 내 모든 날짜 생성
        List<LocalDate> allDates = IntStream.range(0, 30)
                .mapToObj(i -> endDate.minusDays(i))
                .sorted(Comparator.reverseOrder()) // 최신 날짜부터 정렬
                .collect(Collectors.toList());

        List<RevenueHistory> history;
        try {
            // 실제 데이터 조회
            history = revenueRepository.findByMemberIdAndDateBetween(memberId, startDate, endDate, PageRequest.of(0, 30))
                    .getContent()
                    .stream()
                    .sorted(Comparator.comparing(RevenueHistory::getDate))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("RevenueHistory 조회 실패: memberId = {}, period = {}-{}, error = {}", memberId, startDate, endDate, e.getMessage(), e);
            history = new ArrayList<>(); // 데이터 없음 처리
        }

        // 실제 데이터와 30일 범위 매핑 (존재하지 않는 날짜는 todayTotal = 0.0으로 처리)
        Map<LocalDate, Double> historyMap = history.stream()
                .collect(Collectors.toMap(RevenueHistory::getDate, RevenueHistory::getTodayTotal, (existing, replacement) -> existing));

        return allDates.stream()
                .map(date -> new RevenueHistoryResponseDto(
                        date,
                        historyMap.getOrDefault(date, 0.0), // 데이터 없으면 0.0으로 기본값
                        member.getName(),
                        isCurrentUser))
                .collect(Collectors.toList());
    }
}