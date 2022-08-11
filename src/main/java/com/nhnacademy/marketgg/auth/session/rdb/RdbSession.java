package com.nhnacademy.marketgg.auth.session.rdb;

import com.nhnacademy.marketgg.auth.session.Session;
import com.nhnacademy.marketgg.auth.session.rdb.entity.SessionId;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * RDB 를 이용한 Session 을 관리하기 위한 클래스
 *
 * @author 윤동열
 */
@NoArgsConstructor
@Getter
public class RdbSession implements Session {

    private SessionId sessionId;
    private Map<String, Object> session;

    public RdbSession(SessionId sessionId) {
        this.sessionId = sessionId;
        this.session = new HashMap<>();
    }

    @Override
    public long getCreationTime() {
        return this.sessionId.getCreationTime();
    }

    @Override
    public String getId() {
        return this.sessionId.getId();
    }

    @Override
    public long getLastAccessedTime() {
        return this.sessionId.getLastAccessedTime();
    }

    @Override
    public void setMaxInactiveInterval(int var1) {
        this.sessionId.setMaxInactiveInterval(var1);
    }

    @Override
    public int getMaxInactiveInterval() {
        return this.sessionId.getMaxInactiveInterval();
    }

    @Override
    public Object getAttribute(String var1) {
        return this.session.get(var1);
    }

    @Override
    public void setAttribute(String var1, Object var2) {
        this.session.put(var1, var2);
    }

    @Override
    public void removeAttribute(String var1) {
        this.session.remove(var1);
    }

    @Override
    public void invalidate() {
        this.session.clear();
    }

    @Override
    public boolean isNew() {
        return true;
    }

    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

}
