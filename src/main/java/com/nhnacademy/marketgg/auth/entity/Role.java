package com.nhnacademy.marketgg.auth.entity;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor
public class Role {

    @Id
    @Column(name = "role_no")
    private Long roleNo;

    @Enumerated(EnumType.STRING)
    @Column
    private Roles name;
}