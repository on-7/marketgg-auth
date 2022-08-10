package com.nhnacademy.marketgg.auth.session.rdb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class SessionId {

    @Id
    private String sessionId;

    public static SessionId of(String sessionId) {
        SessionId session = new SessionId();
        session.sessionId = sessionId;
        return session;
    }

}
