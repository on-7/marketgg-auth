package com.nhnacademy.marketgg.auth.session.rdb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 세션을 관리하기 위한 Entity.
 *
 * @author 윤동열
 */
@Entity
@Getter
@ToString
public class SessionId {

    @Id
    private String id;

    private long creationTime;

    @Setter
    private long lastAccessedTime;

    @Setter
    private int maxInactiveInterval;

    protected SessionId() {
        this.creationTime = System.currentTimeMillis();
        this.maxInactiveInterval = 30;
    }

    public static SessionId of(String sessionId) {
        SessionId session = new SessionId();
        session.id = sessionId;
        return session;
    }

    public boolean isNonExpired() {
        long cur = System.currentTimeMillis();
        return (creationTime + maxInactiveInterval) < cur;
    }

}
