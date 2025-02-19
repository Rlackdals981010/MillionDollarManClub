package com.dolloer.million.domain.log.repository;

import com.dolloer.million.domain.log.entity.RevenueHistory;
import com.dolloer.million.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RevenueRepository extends JpaRepository<RevenueHistory, Long> {

    Page<RevenueHistory> findByMemberId(Long memberId, Pageable pageable);

    List<RevenueHistory> findByMemberAndDateBetween(Member member, LocalDate localDate, LocalDate localDate1);
}
