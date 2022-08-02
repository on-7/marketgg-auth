package com.nhnacademy.marketgg.auth.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 어떤 컨트롤러에 진입했는지 로그를 남기는 클래스입니다.
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    /**
     * 진입 메서드에 대한 로그를 남깁니다.
     * @param jp - 메서드 정보
     */
    @Before("execution(* com.nhnacademy.marketgg.auth.controller.*.*(*))")
    public void logEntryMethod(JoinPoint jp) {
        log.info("Controller Method: {}", jp.getSignature().getName());
    }

}
