package com.nhnacademy.marketgg.auth.session.rdb;

import com.nhnacademy.marketgg.auth.session.Session;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RdbSession implements Session {

    private String id;
    private Map<String, Object> session = new HashMap<>();
    private long creationTime;
    private long lastAccessedTime;
    private int maxInactiveInterval;

    @Override
    public long getCreationTime() {
        return 0;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public long getLastAccessedTime() {
        return 0;
    }

    @Override
    public void setMaxInactiveInterval(int var1) {

    }

    @Override
    public int getMaxInactiveInterval() {
        return 0;
    }

    @Override
    public Object getAttribute(String var1) {
        return null;
    }

    @Override
    public void setAttribute(String var1, Object var2) {

    }

    @Override
    public void removeAttribute(String var1) {

    }

    @Override
    public void invalidate() {

    }

    @Override
    public boolean isNew() {
        return false;
    }
}
