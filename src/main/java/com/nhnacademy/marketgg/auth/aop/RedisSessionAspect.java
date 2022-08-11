package com.nhnacademy.marketgg.auth.aop;

import com.nhnacademy.marketgg.auth.session.Session;
import com.nhnacademy.marketgg.auth.session.SessionContext;
import com.nhnacademy.marketgg.auth.session.redis.RedisSession;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

/**
 * Redis 를 이용한 세션을 다루기 위한 AOP.
 *
 * {@inheritDoc}
 *
 * @author 윤동열
 */
@Slf4j
@Aspect
@Profile("redis")
@Component
@RequiredArgsConstructor
public class RedisSessionAspect implements SessionAspect {

    private final RedisTemplate<String, RedisSession> redisTemplate;

    @Override
    @Around("execution(* *(.., com.nhnacademy.marketgg.auth.session.Session, ..))")
    public Object session(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        log.info("Method = {}", signature.getName());

        HttpServletRequest request = this.getRequest();
        String sessionId = SessionContext.get().orElse(request.getSession().getId());

        // 로그아웃 시 세션 삭제
        if (Objects.equals(signature.getName(), "logout")) {
            redisTemplate.delete(sessionId);
            return pjp.proceed();
        }

        RedisSession session = Optional.ofNullable(redisTemplate.opsForValue().get(sessionId))
                                       .orElse(new RedisSession(sessionId));

        Object[] objects = Arrays.stream(pjp.getArgs())
                                 .map(arg -> {
                                     if (arg instanceof Session) {
                                         arg = session;
                                     }
                                     return arg;
                                 }).toArray();

        Object proceed = pjp.proceed(objects);  // 메서드 실행

        // 로그인 한 세션만 저장
        if (Objects.nonNull(session.getAttribute(Session.MEMBER))) {
            redisTemplate.opsForValue()
                         .set(sessionId, session, 30, TimeUnit.SECONDS);
        }

        return proceed;
    }

    @Override
    @Around("execution(public void com.nhnacademy.marketgg.auth.session.redis.RedisSession.setMaxInactiveInterval(int))")
    public Object maxInactiveInterval(ProceedingJoinPoint pjp) throws Throwable {

        Optional<String> opSessionId = SessionContext.get();

        if (opSessionId.isEmpty()) {
            return null;
        }
        String sessionId = opSessionId.get();

        RedisSession session = redisTemplate.opsForValue().get(sessionId);

        if (Objects.isNull(session)) {
            return null;
        }

        Object proceed = pjp.proceed();

        int maxInactiveInterval = session.getMaxInactiveInterval();

        redisTemplate.opsForValue().set(sessionId, session, maxInactiveInterval, TimeUnit.SECONDS);

        return proceed;
    }

    @Override
    @Before("@within(controller) && !execution(* *.*logout*(*))")
    public void setLastAccessedTime(JoinPoint jp, Controller controller) {
        log.info("Method = {}", jp.getSignature().getName());

        Optional<String> opSessionId = SessionContext.get();

        if (opSessionId.isEmpty()) {
            return;
        }

        String sessionId = opSessionId.get();
        RedisSession redisSession = redisTemplate.opsForValue().get(sessionId);

        if (Objects.isNull(redisSession)) {
            return;
        }

        redisSession.setLastAccessedTime(System.currentTimeMillis());

        redisTemplate.opsForValue()
                     .set(sessionId, redisSession, 30, TimeUnit.SECONDS);
    }

}
