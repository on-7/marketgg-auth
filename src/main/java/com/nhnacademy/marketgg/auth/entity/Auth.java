package com.nhnacademy.marketgg.auth.entity;

import com.nhnacademy.marketgg.auth.dto.SignupRequestDto;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "auth")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column
    @Enumerated(EnumType.STRING)
    private Roles role;

    public Auth(SignupRequestDto signupRequestDto) {
        this.username = signupRequestDto.getUsername();
        this.password = signupRequestDto.getPassword();
        this.email = signupRequestDto.getEmail();
        this.name = signupRequestDto.getName();
        this.passwordUpdatedAt = LocalDate.now();
        this.provider = Provider.SELF;
        this.role = Roles.ROLE_USER;
    }

}
