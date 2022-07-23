package com.nhnacademy.marketgg.auth.aspect;

import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import java.util.Arrays;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TokenAspect {

    private final TokenUtils tokenUtils;

    @Around("execution(* com.nhnacademy.marketgg.auth.controller.*.*(.., @com.nhnacademy.marketgg.auth.annotation.Token (*), ..))")
    public Object parseToken(ProceedingJoinPoint pjp) throws Throwable {
        ServletRequestAttributes requestAttributes =
            Objects.requireNonNull(
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes());

        String token = requestAttributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);

        if (Objects.isNull(token)
            || !token.startsWith(TokenUtils.BEARER)
            || tokenUtils.isInvalidToken(token)) {

            throw new IllegalArgumentException();
        }

        String jwt = token.substring(TokenUtils.BEARER_LENGTH);

        Object[] args = Arrays.stream(pjp.getArgs())
                              .map(arg -> {
                                  if (arg instanceof String) {
                                      arg = jwt;
                                  }
                                  return arg;
                              }).toArray();

        return pjp.proceed(args);
    }
}
