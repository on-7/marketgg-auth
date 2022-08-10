package com.nhnacademy.marketgg.auth.session;

import java.util.Optional;

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
