package com.nhnacademy.marketgg.auth.aop;

import com.nhnacademy.marketgg.auth.session.rdb.AttributeRepository;
import com.nhnacademy.marketgg.auth.session.rdb.SessionIdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Slf4j
@Aspect
@Profile("rdb")
@Component
@RequiredArgsConstructor
public class RdbSessionAspect implements SessionAspect {

    private final SessionIdRepository sessionIdRepository;
    private final AttributeRepository attributeRepository;

    @Override
    public Object session(ProceedingJoinPoint pjp) throws Throwable {
        return null;
    }

    @Override
    public Object maxInactiveInterval(ProceedingJoinPoint pjp) throws Throwable {
        return null;
    }

    @Override
    public void setLastAccessedTime(JoinPoint jp, Controller controller) throws NoSuchFieldException {

    }

}
