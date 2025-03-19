package com.dolloer.million.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 메서드에만 적용 가능
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지 (AOP에서 감지 가능)
public @interface LogExecution {
}