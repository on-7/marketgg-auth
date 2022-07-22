package com.nhnacademy.marketgg.auth.entity;

import com.nhnacademy.marketgg.auth.constant.Provider;
import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import com.nhnacademy.marketgg.auth.dto.request.UpdateRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Table(name = "auth")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_no")
    private Long id;

    @Column(unique = true)
    private String uuid;

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

    public Auth(final SignUpRequest signUpRequest) {
        this.uuid = UUID.randomUUID().toString();
        this.email = signUpRequest.getEmail();
        this.password = signUpRequest.getPassword();
        this.name = signUpRequest.getName();
        this.phoneNumber = signUpRequest.getPhoneNumber();
        this.passwordUpdatedAt = LocalDate.now();
        this.provider = Provider.SELF;
    }

    public Auth(final UpdateRequest updateRequest,String uuid) {
        this.uuid = uuid;
        this.email = updateRequest.getEmail();
        this.password = updateRequest.getPassword();
        this.name = updateRequest.getName();
        this.phoneNumber = updateRequest.getPhoneNumber();
        this.passwordUpdatedAt = getUpdateDate(updateRequest.getPassword());
        this.provider = Provider.SELF;
    }

    /**
     * 패스워드 수정 됬는지 확인하는 메서드입니다.
     *
     * @param updatedPassword - 수정된 비밀번호 입니다.
     * @return LocalDate - 비밀번호가 수정된 날짜를 기점으로 갱신합니다.
     */
    private LocalDate getUpdateDate(String updatedPassword) {
        if (isUpdatePassword(updatedPassword)) {
            return this.passwordUpdatedAt;
        }

        return LocalDate.now();
    }

    /**
     * 패스워드가 Null 인지, 기존 비밀번호랑 같은지 체크하는 메서드입니다.
     *
     * @param updatedPassword - 수정된 비밀번호 입니다.
     * @return boolean - Null 이 아니고, 기존 비밀번호랑 같으면 false 를 반환.
     */
    private boolean isUpdatePassword(String updatedPassword) {
        return Objects.isNull(updatedPassword) || Objects.equals(this.password, updatedPassword);
    }

}
