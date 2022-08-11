package com.nhnacademy.marketgg.auth.aop;

import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 세션을 다루기 위한 AOP 에 대한 인터페이스.
 *
 * @author 윤동열
 */
public interface SessionAspect {

    /**
     * 세션을 요청한 메서드에 대해 세션 주입.
     *
     * @author 윤동열
     */
    Object session(ProceedingJoinPoint pjp) throws Throwable;

    /**
     * 최대 유효 시간 변경 시 DB 반영.
     *
     * @author 윤동열
     */
    Object maxInactiveInterval(ProceedingJoinPoint pjp) throws Throwable;

    /**
     * 마지막 요청 시 lastAccessedTime 변경 사항 DB 반영.
     *
     * @param controller - 요청 받은 Controller
     * @author 윤동열
     */
    void setLastAccessedTime(JoinPoint jp, Controller controller);

    /**
     * 요청 정보를 반횐한다.
     *
     * @return 요청 정보
     * @author 윤동열
     */
    default HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes =
            (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        return requestAttributes.getRequest();
    }

}
