package com.nhnacademy.marketgg.auth.aop;

import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public interface SessionAspect {

    Object session(ProceedingJoinPoint pjp) throws Throwable;

    Object maxInactiveInterval(ProceedingJoinPoint pjp) throws Throwable;

    void setLastAccessedTime(JoinPoint jp, Controller controller) throws Throwable;

    default HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes =
            (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        return requestAttributes.getRequest();
    }

}
