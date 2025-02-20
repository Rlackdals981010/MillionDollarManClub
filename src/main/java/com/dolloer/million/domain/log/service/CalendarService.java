package com.dolloer.million.domain.log.service;

import com.dolloer.million.domain.log.entity.RevenueHistory;
import com.dolloer.million.domain.log.repository.RevenueRepository;
import com.dolloer.million.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;


@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {

    private final RevenueRepository revenueRepository;

    // 월별 캘린더 데이터 조회 (특정 월의 모든 데이터)
    @Transactional(readOnly = true)
    public Page<RevenueHistory> getMonthlyRevenueHistory(Long memberId,String year, String month, Pageable pageable) {

        // year와 month 유효성 검사
        if (year == null || month == null || year.trim().isEmpty() || month.trim().isEmpty()) {
            throw new IllegalArgumentException("연도와 월은 필수 입력값입니다.");
        }

        // year가 4자리 숫자인지 확인
        if (!year.matches("\\d{4}")) {
            throw new IllegalArgumentException("연도는 4자리 숫자여야 합니다. 예: 2023");
        }

        // month가 01~12 사이의 두 자리 숫자인지 확인
        if (!month.matches("^(0[1-9]|1[0-2])$")) {
            throw new IllegalArgumentException("월은 01~12 사이의 두 자리 숫자여야 합니다. 예: 02");
        }

        String date = year + "-" + month;
        log.info("Received month parameter: {}", month);

        LocalDate startDate;
        try {
            startDate = LocalDate.parse(date + "-01");
            log.info("Parsed Start Date: {}", startDate);
        } catch (DateTimeParseException e) {
            log.error("Failed to parse date {}: {}", date, e.getMessage());
            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다. YYYY-MM 형식을 사용하세요.", e);
        }

        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        log.info("Calculated End Date: {}", endDate);

        return revenueRepository.findByMemberIdAndDateBetween(memberId, startDate, endDate, pageable);
    }

    // 특정 날짜의 상세 이력 조회
    @Transactional(readOnly = true)
    public RevenueHistory getRevenueHistoryByDate(Long memberId, LocalDate date) {
        log.info(String.valueOf(date));
        RevenueHistory ret = null;
        try{
            ret = revenueRepository.findByMemberIdAndDate(memberId, date)
                    .orElseThrow(() -> new IllegalArgumentException("해당 날짜의 데이터가 존재하지 않습니다."));
        }catch (Exception e){
            log.info(e.getMessage());
        }


        return ret;
    }
}
