package com.dolloer.million.domain.log.repository;

import com.dolloer.million.domain.log.entity.SeedHistory;
import com.dolloer.million.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SeedRepository extends JpaRepository<SeedHistory, Long> {

    Page<SeedHistory> findByMemberId(Long memberId, Pageable pageable);


    Optional<SeedHistory> findByMemberAndDate(Member member, LocalDate today);
    Optional<SeedHistory> findTopByMemberIdAndDateBeforeOrderByDateDesc(Long memberId, LocalDate date);
}
