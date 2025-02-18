package com.dolloer.million.domain.log.repository;

import com.dolloer.million.domain.log.entity.RevenueHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevenueRepository extends JpaRepository<RevenueHistory, Long> {

    Page<RevenueHistory> findByMemberId(Long memberId, Pageable pageable);
}
