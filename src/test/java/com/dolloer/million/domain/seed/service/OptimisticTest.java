package com.dolloer.million.domain.member.service;

import com.dolloer.million.domain.member.entity.Member;
import com.dolloer.million.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class OptimisticTest {

    private static final Logger log = LoggerFactory.getLogger(OptimisticTest.class); // Logger 객체 선언


    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void 최초_시드설정_성공() {
        // when
        memberService.setSeedMoney(1L, 1000.0); // 서비스 메서드 호출
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("등록하고오셈"));
        // then
        assertEquals(1000.0, member.getSeedMoney()); // 시드 금액이 설정되었는지 확인
    }

    @Test
    public void 시드세팅_낙관적락_테스트() throws InterruptedException {
        int threadCount = 10; // 동시에 실행할 스레드 수
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount); // 모든 스레드가 끝날 때까지 기다림

        // 멀티 스레드 환경에서 setSeedMoney 호출
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // 낙관적 락을 적용한 메서드 호출
                    memberService.setSeedMoney(1L, 1000.0);
                }  finally {
                    latch.countDown(); // 스레드 완료 시 카운트 다운
                }
            });
        }

        // 모든 스레드가 끝날 때까지 기다림
        latch.await();
    }

    @Test
    public void setRevenue_낙관적락_테스트() throws InterruptedException {
        int threadCount = 10; // 동시에 실행할 스레드 수
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount); // 모든 스레드가 끝날 때까지 기다림

        // 멀티 스레드 환경에서 setRevenue 호출
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // 낙관적 락을 적용한 메서드 호출
                    memberService.setRevenue(1L, 3000.0,300.0);
                }catch (ObjectOptimisticLockingFailureException e) {
                    log.info(e.getMessage());
                }
                finally {
                    latch.countDown(); // 스레드 완료 시 카운트 다운
                }
            });
        }

        // 모든 스레드가 끝날 때까지 기다림
        latch.await();
    }


}
