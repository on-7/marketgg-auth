package com.nhnacademy.marketgg.auth.entity;

import com.nhnacademy.marketgg.auth.constant.Roles;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor
public class Role {

    @NotNull
    @Id
    @Column(name = "role_no")
    private Long roleNo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column
    private Roles name;

}
