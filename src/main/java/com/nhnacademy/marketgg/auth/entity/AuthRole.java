package com.nhnacademy.marketgg.auth.entity;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "auth_roles")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AuthRole {

    @EmbeddedId
    private Pk id;

    @MapsId("authNo")
    @ManyToOne
    @JoinColumn(name = "auth_no")
    private Auth auth;

    @MapsId("roleNo")
    @ManyToOne
    @JoinColumn(name = "role_no")
    private Role role;

    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pk implements Serializable {

        private Long authNo;
        private Long roleNo;
    
    }

}
