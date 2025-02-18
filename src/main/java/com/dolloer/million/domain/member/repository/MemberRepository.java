package com.dolloer.million.domain.member.repository;

import com.dolloer.million.domain.member.entity.Member;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

        Optional<Member> findByName(String name);

        @Lock(LockModeType.OPTIMISTIC)
        @Query("select m from Member m where m.id=:id")
        Optional<Member> findByIdWithLock(@Param("id")Long id);
}
