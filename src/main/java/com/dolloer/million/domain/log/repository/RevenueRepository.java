package com.dolloer.million.domain.log.repository;

import com.dolloer.million.domain.log.entity.RevenueHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface RevenueRepository extends JpaRepository<RevenueHistory, Long> {

    Page<RevenueHistory> findByMemberId(Long memberId, Pageable pageable);

    Page<RevenueHistory> findByMemberIdAndDateBetween(Long memberId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Optional<RevenueHistory> findByMemberIdAndDate(Long memberId, LocalDate date);


}
