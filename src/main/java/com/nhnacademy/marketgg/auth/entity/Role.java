package com.nhnacademy.marketgg.auth.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "role")
@Getter
@NoArgsConstructor
public class Role {

    @Id
    @Column(name = "role_no")
    private Long roleNo;

    @Column
    private String name;
}