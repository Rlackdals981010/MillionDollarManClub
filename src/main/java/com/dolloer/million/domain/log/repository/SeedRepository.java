package com.dolloer.million.domain.seed.repository;

import com.dolloer.million.domain.seed.entity.SeedHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeedRepository extends JpaRepository<SeedHistory, Long> {

    Page<SeedHistory> findByMemberId(Long memberId, Pageable pageable);



}
