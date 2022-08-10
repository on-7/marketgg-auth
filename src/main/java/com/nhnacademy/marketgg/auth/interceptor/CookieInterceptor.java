package com.nhnacademy.marketgg.auth.interceptor;

import com.nhnacademy.marketgg.auth.session.Session;
import com.nhnacademy.marketgg.auth.session.SessionContext;
import java.util.Objects;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class CookieInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

        SessionContext.set(getSessionId(request.getCookies()));

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

        SessionContext.remove();
    }

    private String getSessionId(Cookie[] cookies) {

        if (Objects.isNull(cookies)) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (Objects.equals(Session.SESSION_ID, cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

}
