package com.nhnacademy.marketgg.auth.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor
public class Role {

    @Id
    @Column(name = "role_no")
    private Long roleNo;

    @Column
    @Enumerated(EnumType.STRING)
    private Roles name;
}