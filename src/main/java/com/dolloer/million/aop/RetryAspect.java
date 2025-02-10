package com.dolloer.million.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RetryAspect {

    @Around("@annotation(retryAnnotation)")
    public Object retry(ProceedingJoinPoint joinPoint, Retry retryAnnotation) throws Throwable {
        int maxAttempts = retryAnnotation.maxAttempts();
        long delay = retryAnnotation.delay();
        int attempt = 0;

        while (true) {
            try {
                attempt++;
                return joinPoint.proceed();  // 원래 메서드 실행
            } catch (Exception e) {
                if (attempt >= maxAttempts) {
                    throw e; // 최대 시도 횟수를 초과하면 예외 발생
                }
                System.out.println("재시도 " + attempt + "회 실패. " + delay + "ms 후 재시도...");
                Thread.sleep(delay);  // 딜레이 후 재시도
            }
        }
    }
}