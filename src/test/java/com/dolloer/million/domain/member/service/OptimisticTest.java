package com.dolloer.million.domain.member.service;

import com.dolloer.million.domain.member.entity.Member;
import com.dolloer.million.domain.member.repository.MemberRepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class OptimisticTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    private Long memberId;
    Member member;
    @BeforeEach
    void setUp() {
        member = new Member("test");
        member = memberRepository.save(member);
        memberId = member.getId();
    }

    @Test
    void 동시_업데이트_낙관적_락_테스트() throws InterruptedException {
        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Runnable task = () -> {
            try {
                memberService.setSeedMoney(memberId, 200.0);
            } catch (OptimisticLockException e) {
                System.out.println("OptimisticLockException 발생!");
            } finally {
                latch.countDown();
            }
        };

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(task);
        }

        memberService.setSeedMoney(memberId, 200.0);
        assertEquals(200.0, member.getSeedMoney());

        latch.await();
        executorService.shutdown();
    }
}
