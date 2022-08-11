package com.nhnacademy.marketgg.auth.session.rdb.entity;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Attribute {

    @EmbeddedId
    private Id id;

    @MapsId("sessionId")
    @ManyToOne
    @JoinColumn(name = "session_id")
    private SessionId session;

    private String value;

    public Attribute(SessionId session, String attributeKey, String value) {
        this.id = new Id(session.getId(), attributeKey);
        this.session = session;
        this.value = value;
    }

    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Id implements Serializable {

        private String sessionId;
        private String attributeKey;

    }

}
