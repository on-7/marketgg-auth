package com.nhnacademy.marketgg.auth.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "auth_roles")
@Entity
@Getter
@NoArgsConstructor
public class AuthRoles {

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