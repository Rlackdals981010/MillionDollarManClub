package com.dolloer.million.aop;

import com.dolloer.million.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        String username = getCurrentUsername(); // 현재 로그인한 사용자 가져오기
        log.info("[{}][{}] 호출",username, methodName);

        try{
            Object result = joinPoint.proceed();
            log.info("[{}][{}] 응답", username,methodName);
            return result;
        }catch (Throwable ex){
            log.error("[{}] 실행중 오류 발생: {}", methodName, ex.getMessage());
            throw ex;
        }
    }

    // 현재 로그인한 사용자 이름 가져오기
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthUser) {
            return ((AuthUser) authentication.getPrincipal()).getName();
        }
        return "Anonymous"; // 인증되지 않은 사용자
    }
}
