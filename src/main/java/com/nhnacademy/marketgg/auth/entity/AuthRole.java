package com.nhnacademy.marketgg.auth.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * 회원과 권한의 관계를 맺는 클래스입니다.
 *
 * @version 1.0.0
 */
@Table(name = "auth_roles")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class AuthRole {

    @EmbeddedId
    private Pk id;

    @NotNull
    @MapsId("authId")
    @ManyToOne
    @JoinColumn(name = "auth_no")
    private Auth auth;

    @NotNull
    @MapsId("roleId")
    @ManyToOne
    @JoinColumn(name = "role_no")
    private Role role;

    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pk implements Serializable {

        private Long authId;
        private Long roleId;

    }

}
