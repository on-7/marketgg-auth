package com.nhnacademy.marketgg.auth.entity;

import com.nhnacademy.marketgg.auth.constant.Provider;
import com.nhnacademy.marketgg.auth.dto.request.SignupRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Table(name = "auth")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Auth {

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_no")
    private Long authNo;

    @NotBlank
    @Length(max = 30)
    @Column
    private String email;

    @NotBlank
    @Length(max = 255)
    @Column
    private String password;

    @NotBlank
    @Length(max = 15)
    @Column
    private String name;

    @NotBlank
    @Length(max = 15)
    @Column
    private String phoneNumber;

    @NotNull
    @Column(name = "password_updated_at")
    private LocalDate passwordUpdatedAt;

    @NotNull
    @Column
    @Enumerated(EnumType.STRING)
    private Provider provider;

    public Auth(SignupRequest signupRequest) {
        this.email = signupRequest.getEmail();
        this.password = signupRequest.getPassword();
        this.name = signupRequest.getName();
        this.phoneNumber = signupRequest.getPhoneNumber();
        this.passwordUpdatedAt = LocalDate.now();
        this.provider = Provider.SELF;
    }

}
