package com.nhnacademy.marketgg.auth.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Before("execution(* com.nhnacademy.marketgg.auth.controller.*.*(*))")
    public void logEntryMethod(JoinPoint jp) {
        log.info("Method: {}", jp.getSignature().getName());
    }

}
