package com.dolloer.million.domain.log.repository;

import com.dolloer.million.domain.log.entity.RevenueHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RevenueRepository extends JpaRepository<RevenueHistory, Long> {

    Page<RevenueHistory> findByMemberId(Long memberId, Pageable pageable);

    List<RevenueHistory> findByMemberIdAndDateBetween(Long memberId, LocalDate startDate, LocalDate endDate);

    Optional<RevenueHistory> findByMemberIdAndDate(Long memberId, LocalDate date);


    Optional<RevenueHistory> findTopByMemberIdAndDateBeforeOrderByDateDesc(Long memberId, LocalDate date);

    List<RevenueHistory> findByMemberIdAndDateAfterOrderByDateAsc(Long memberId, LocalDate date);

    List<RevenueHistory> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
