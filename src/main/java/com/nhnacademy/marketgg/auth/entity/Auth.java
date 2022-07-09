package com.nhnacademy.marketgg.auth.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "auth")
@Getter
@NoArgsConstructor
public class Auth {

    @Id
    @Column(name = "auth_no")
    private Long authNo;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String email;

    @Column
    private String name;

    @Column(name = "password_updated_at")
    private LocalDate passwordUpdatedAt;

    @Column
    private String provider;

    @Column
    private String role;

}