package com.nhnacademy.marketgg.auth.session;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * ThreadLocal 을 이용하여 Interceptor 에서 Cookie 가 가지고 있는 세션 정보를 다룬다.
 *
 * @author 윤동열
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionContext {

    private static final ThreadLocal<Optional<String>> threadLocal = new ThreadLocal<>();

    public static void set(String sessionId) {
        threadLocal.set(Optional.ofNullable(sessionId));
    }

    public static Optional<String> get() {
        return Optional.ofNullable(threadLocal.get())
                       .orElse(Optional.empty());
    }

    public static void remove() {
        threadLocal.remove();
    }

}
