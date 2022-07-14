package com.nhnacademy.marketgg.auth.entity;

import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "auth_roles")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class AuthRole {

    @EmbeddedId
    private Pk id;

    @NotNull
    @MapsId("authNo")
    @ManyToOne
    @JoinColumn(name = "auth_no")
    private Auth auth;

    @NotNull
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
