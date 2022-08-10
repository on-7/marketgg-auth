package com.nhnacademy.marketgg.auth.session.redis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nhnacademy.marketgg.auth.session.Session;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RedisSession implements Session {

    private String id;
    private Map<String, Object> session;
    private long creationTime;
    private long lastAccessedTime;
    private int maxInactiveInterval;

    public RedisSession(String id) {
        this.id = id;
        this.session = new HashMap<>();
        this.creationTime = System.currentTimeMillis();
        this.maxInactiveInterval = 30;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public long getLastAccessedTime() {
        return this.lastAccessedTime;
    }

    @Override
    public void setMaxInactiveInterval(int var1) {
        this.maxInactiveInterval = var1;
    }

    @Override
    public int getMaxInactiveInterval() {
        return this.maxInactiveInterval;
    }

    @Override
    public Object getAttribute(String var1) {
        return session.get(var1);
    }

    @Override
    public void setAttribute(String var1, Object var2) {
        session.put(var1, var2);
    }

    @Override
    public void removeAttribute(String var1) {
        session.remove(var1);
    }

    @Override
    public void invalidate() {
        session.clear();
    }

    @JsonIgnore
    @Override
    public boolean isNew() {
        return this.session.containsKey(MEMBER);
    }

}
