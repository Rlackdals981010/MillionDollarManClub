package com.dolloer.million.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {
    int maxAttempts() default 3;  // 최대 재시도 횟수 (기본값: 3)
    long delay() default 1000;    // 재시도 간격 (기본값: 1000ms = 1초)
}