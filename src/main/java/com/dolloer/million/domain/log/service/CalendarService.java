package com.dolloer.million.domain.log.service;

import com.dolloer.million.domain.log.dto.response.RevenueResponseDto;
import com.dolloer.million.domain.log.entity.RevenueHistory;
import com.dolloer.million.domain.log.repository.RevenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {

    private final RevenueRepository revenueRepository;

    // 월별 캘린더 데이터 조회 (특정 월의 모든 데이터)
    public Map<LocalDate, Boolean> getMonthlyQuestStatus(Long memberId, String year, String month) {
        checkDate(year, month);

        String date = year + "-" + month;
        LocalDate startDate;
        try {
            startDate = LocalDate.parse(date + "-01");
        } catch (DateTimeParseException e) {
            log.error("Failed to parse date {}: {}", date, e.getMessage());
            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다. YYYY-MM 형식을 사용하세요.", e);
        }

        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // 해당 월의 모든 날짜에 대해 quest 상태 조회
        List<RevenueHistory> histories = revenueRepository.findByMemberIdAndDateBetween(memberId, startDate, endDate);

        // 각 날짜별 quest 상태를 Map으로 반환
        Map<LocalDate, Boolean> questStatus = new HashMap<>();
        Map<LocalDate, Boolean> historyMap = Optional.ofNullable(histories)
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(RevenueHistory::getDate, RevenueHistory::getQuest, (existing, replacement) -> existing));

        for (LocalDate dateIterator = startDate; !dateIterator.isAfter(endDate); dateIterator = dateIterator.plusDays(1)) {
            Boolean quest = historyMap.getOrDefault(dateIterator, false); // 데이터 없으면 false
            questStatus.put(dateIterator, quest);
        }

        return questStatus;
    }
    // 특정 날짜의 상세 이력 조회
    @Transactional(readOnly = true)
    public List<RevenueResponseDto> getMonthlyRevenueDetails(Long memberId, String year, String month) {
        checkDate(year, month);

        String date = year + "-" + month;
        LocalDate startDate;
        try {
            startDate = LocalDate.parse(date + "-01");
        } catch (DateTimeParseException e) {
            log.error("Failed to parse date {}: {}", date, e.getMessage());
            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다. YYYY-MM 형식을 사용하세요.", e);
        }

        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        // 해당 월의 모든 날짜에 대해 RevenueHistory 리스트 생성
        List<RevenueHistory> histories = revenueRepository.findByMemberIdAndDateBetween(memberId, startDate, endDate);

        // histories를 LocalDate를 키로, RevenueHistory를 값으로 하는 Map으로 변환
        Map<LocalDate, RevenueHistory> historyMap = Optional.ofNullable(histories)
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(RevenueHistory::getDate, history -> history, (existing, replacement) -> existing));

        // 모든 날짜에 대해 데이터가 있는지 확인하고, 없으면 기본값으로 초기화
        List<RevenueResponseDto> monthlyDetails = new ArrayList<>();
        for (LocalDate dateIterator = startDate; !dateIterator.isAfter(endDate); dateIterator = dateIterator.plusDays(1)) {
            RevenueHistory history = historyMap.getOrDefault(dateIterator, null);
            RevenueResponseDto dto = history != null
                    ? RevenueResponseDto.from(history)
                    : new RevenueResponseDto(dateIterator, 0.0, 0.0, 0.0, 0.0, false);
            monthlyDetails.add(dto);
        }

        return monthlyDetails;
    }

    private void checkDate(String year, String month){
        // year와 month 유효성 검사
        if (year == null || month == null || year.trim().isEmpty() || month.trim().isEmpty()) {
            throw new IllegalArgumentException("연도와 월은 필수 입력값입니다.");
        }

        if (!year.matches("\\d{4}")) {
            throw new IllegalArgumentException("연도는 4자리 숫자여야 합니다. 예: 2023");
        }

        if (!month.matches("^(0[1-9]|1[0-2])$")) {
            throw new IllegalArgumentException("월은 01~12 사이의 두 자리 숫자여야 합니다. 예: 02");
        }
    }
}
