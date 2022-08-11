package com.nhnacademy.marketgg.auth.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.auth.session.Session;
import com.nhnacademy.marketgg.auth.session.SessionContext;
import com.nhnacademy.marketgg.auth.session.rdb.AttributeRepository;
import com.nhnacademy.marketgg.auth.session.rdb.RdbSession;
import com.nhnacademy.marketgg.auth.session.rdb.SessionIdRepository;
import com.nhnacademy.marketgg.auth.session.rdb.entity.Attribute;
import com.nhnacademy.marketgg.auth.session.rdb.entity.SessionId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

/**
 * MySQL 을 이용한 세션을 이용하기 위한 AOP.
 *
 * {@inheritDoc}
 *
 * @author 윤동열
 */
@Slf4j
@Aspect
@Profile("rdb")
@Component
@RequiredArgsConstructor
public class RdbSessionAspect implements SessionAspect {

    private final SessionIdRepository sessionIdRepository;
    private final AttributeRepository attributeRepository;
    private final ObjectMapper mapper;

    @Override
    @Around("execution(* *(.., com.nhnacademy.marketgg.auth.session.Session, ..))")
    public Object session(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        log.info("Method = {}", signature.getName());

        HttpSession httpSession = this.getRequest().getSession();

        String sid = SessionContext.get().orElse(httpSession.getId());

        SessionId sessionId = sessionIdRepository.findById(sid)
                                                 .filter(SessionId::isNonExpired)
                                                 .orElse(sessionIdRepository.save(SessionId.of(sid)));

        if (Objects.equals(signature.getName(), "logout")) {
            attributeRepository.deleteAll(attributeRepository.findAttributesBySessionId(sessionId.getId()));
            sessionIdRepository.delete(sessionId);
        }

        RdbSession rdbSession = new RdbSession(sessionId);

        HashMap<String, Object> session = new HashMap<>();
        attributeRepository.findAttributesBySessionId(sessionId.getId())
                           .forEach(a -> {
                               try {
                                   Object value = mapper.readValue(a.getValue(), Object.class);
                                   session.put(a.getId().getAttributeKey(), value);
                               } catch (JsonProcessingException e) {
                                   throw new RuntimeException(e);
                               }
                           });

        rdbSession.setSession(session);

        Object[] args = Arrays.stream(pjp.getArgs())
                              .map(arg -> {
                                  if (arg instanceof Session) {
                                      arg = rdbSession;
                                  }
                                  return arg;
                              }).toArray();

        Object proceed = pjp.proceed(args);

        if (rdbSession.getSession().containsKey(Session.MEMBER)) {
            rdbSession.getSession()
                      .keySet()
                      .forEach(k -> {
                          try {
                              String value = mapper.writeValueAsString(rdbSession.getSession().get(k));
                              Attribute attribute = new Attribute(sessionId, k, value);
                              attributeRepository.save(attribute);
                          } catch (JsonProcessingException e) {
                              throw new RuntimeException(e);
                          }
                      });
        } else {
            sessionIdRepository.delete(sessionId);
        }

        return proceed;
    }

    @Override
    @Around("execution(public void com.nhnacademy.marketgg.auth.session.redis.RedisSession.setMaxInactiveInterval(int)) ")
    public Object maxInactiveInterval(ProceedingJoinPoint pjp) throws Throwable {
        // 세션 만료시간이 변경되면 Redis 는 변경해줘야 하지만 RDB 는 따로 변경할 것이 없다.
        return pjp.proceed();
    }

    @Override
    @Before("@within(controller) && !execution(* *.*logout*(*))")
    public void setLastAccessedTime(JoinPoint jp, Controller controller) {
        log.info("Method = {}", jp.getSignature().getName());

        Optional<String> opSessionId = SessionContext.get();

        if (opSessionId.isEmpty()) {
            return;
        }

        Optional<SessionId> byId = sessionIdRepository.findById(opSessionId.get());

        if (byId.isEmpty()) {
            return;
        }

        SessionId sessionId = byId.get();
        sessionId.setLastAccessedTime(System.currentTimeMillis());
    }

}
