package com.nhnacademy.marketgg.auth.entity;

import com.nhnacademy.marketgg.auth.constant.Provider;
import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import java.time.LocalDate;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Table(name = "auth")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_no")
    private Long id;

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

    public Auth(SignUpRequest signUpRequest) {
        this.email = signUpRequest.getEmail();
        this.password = signUpRequest.getPassword();
        this.name = signUpRequest.getName();
        this.phoneNumber = signUpRequest.getPhoneNumber();
        this.passwordUpdatedAt = LocalDate.now();
        this.provider = Provider.SELF;
    }

}
