package com.nhnacademy.marketgg.auth.entity;

import com.nhnacademy.marketgg.auth.constant.Roles;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 권한의 종류를 다루는 클래스입니다.
 *
 * @version 1.0.0
 */
@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor
public class Role {

    @NotNull
    @Id
    @Column(name = "role_no")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column
    private Roles name;

}
