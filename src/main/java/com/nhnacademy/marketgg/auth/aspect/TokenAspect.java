package com.nhnacademy.marketgg.auth.aspect;

import com.nhnacademy.marketgg.auth.annotation.Token;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import java.lang.reflect.Parameter;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 토큰을 자동으로 파싱 및 검증하여 필요한 컨트롤러에 파라미터로 전달합니다.
 *
 * @version 1.0.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TokenAspect {

    private final TokenUtils tokenUtils;

    /**
     * 토큰을 파싱 및 검증합니다.
     *
     * @param pjp - 메서드 원본의 정보를 가지고있는 객체입니다.
     * @return 메서드 정보
     * @throws Throwable 메서드를 실행시킬 때 발생할 수 있는 예외입니다.
     */
    @Around("execution(* com.nhnacademy.marketgg.auth.controller.*.*(.., @com.nhnacademy.marketgg.auth.annotation.Token (*), ..))")
    public Object parseToken(ProceedingJoinPoint pjp) throws Throwable {
        log.info("Method: {}", pjp.getSignature().getName());
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
        log.info("Parsed jwt = {}", jwt);

        Object[] args = pjp.getArgs();

        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Parameter[] parameters = methodSignature.getMethod().getParameters();
        boolean isFound = false;
        for (int i = 0; i < parameters.length && !isFound; i++) {
            Parameter parameter = parameters[i];
            if (parameter.getAnnotation(Token.class) != null) {
                args[i] = jwt;
                break;
            }
        }

        return pjp.proceed(args);
    }

}
