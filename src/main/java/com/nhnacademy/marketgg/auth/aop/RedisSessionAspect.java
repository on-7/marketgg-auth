package com.nhnacademy.marketgg.auth.aop;

import com.nhnacademy.marketgg.auth.session.Session;
import com.nhnacademy.marketgg.auth.session.SessionContext;
import com.nhnacademy.marketgg.auth.session.redis.RedisSession;
import java.lang.reflect.Field;
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
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Profile("redis")
@Component
@RequiredArgsConstructor
public class RedisSessionAspect {

    private final RedisTemplate<String, RedisSession> redisTemplate;

    @Around("execution(* *(.., com.nhnacademy.marketgg.auth.session.Session, ..))")
    public Object session(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        log.info("Method = {}", signature.getName());

        HttpServletRequest request = this.getRequest();
        String sessionId = SessionContext.get().orElse(request.getSession().getId());

        RedisSession session = Optional.ofNullable(redisTemplate.opsForValue().get(sessionId))
                                       .orElse(new RedisSession(sessionId));

        Object[] objects = Arrays.stream(pjp.getArgs())
                                 .map(arg -> {
                                     if (arg instanceof Session) {
                                         arg = session;
                                     }
                                     return arg;
                                 }).toArray();

        Object proceed = pjp.proceed(objects);

        if (Objects.nonNull(session.getAttribute(Session.MEMBER))) {
            redisTemplate.opsForValue()
                         .set(sessionId, session, 30, TimeUnit.SECONDS);
        }

        if (Objects.equals(signature.getName(), "logout")) {
            redisTemplate.delete(sessionId);
        }

        return proceed;
    }

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

        Class<RedisSession> sessionClass = RedisSession.class;
        Field field = sessionClass.getDeclaredField("maxInactiveInterval");
        field.setAccessible(true);
        field.get(session);

        int maxInactiveInterval = (int) field.get(session);

        redisTemplate.opsForValue().set(sessionId, session, maxInactiveInterval, TimeUnit.SECONDS);

        return proceed;
    }

    @Before("@within(controller)")
    public void setLastAccessedTime(JoinPoint jp, Controller controller) throws NoSuchFieldException {
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

        Class<RedisSession> redisSessionClass = RedisSession.class;
        Field field = redisSessionClass.getDeclaredField("lastAccessedTime");
        field.setAccessible(true);
        ReflectionUtils.setField(field, redisSession, System.currentTimeMillis());

        redisTemplate.opsForValue()
                     .set(sessionId, redisSession, 30, TimeUnit.SECONDS);
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes =
            (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        return requestAttributes.getRequest();
    }

}
