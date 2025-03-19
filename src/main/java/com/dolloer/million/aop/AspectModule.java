package com.dolloer.million.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j(topic = "AspectModule")
@Component
@RequiredArgsConstructor
public class AspectModule {

    // 로그
    @Around("@annotation(com.dolloer.million.annotation.LogExecution)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint)throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        log.info("[{}] 호출", methodName);

        try{
            Object result = joinPoint.proceed();
            log.info("[{}] 응답", methodName);
            return result;
        }catch (Throwable ex){
            log.error("[{}] 실행중 오류 발생: {}", methodName, ex.getMessage());
            throw ex;
        }
    }
}
