package com.nhnacademy.marketgg.auth.entity;

import com.nhnacademy.marketgg.auth.constant.Provider;
import com.nhnacademy.marketgg.auth.dto.request.AuthWithDrawRequest;
import com.nhnacademy.marketgg.auth.dto.request.MemberUpdateRequest;
import com.nhnacademy.marketgg.auth.dto.request.signup.SignUpRequest;
import com.nhnacademy.marketgg.auth.exception.AuthNotFoundException;
import com.nhnacademy.marketgg.auth.exception.WithdrawMemberException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
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
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 회원의 정보를 저장합니다.
 *
 * @version 1.0.0
 */
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

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 회원가입에 필요한 정보를 받아 인증 객체를 생성하는 생성자입니다.
     *
     * @param signUpRequest - 회원가입 요청 정보 객체
     */
    public Auth(final SignUpRequest signUpRequest) {
        this.uuid = UUID.randomUUID().toString();
        this.email = signUpRequest.getEmail();
        this.password = signUpRequest.getPassword();
        this.name = signUpRequest.getName();
        this.phoneNumber = signUpRequest.getPhoneNumber();
        this.passwordUpdatedAt = LocalDate.now();
        this.provider =
            checkProvider(signUpRequest.getProvider()) ? Provider.valueOf(signUpRequest.getProvider()) :
                Provider.SELF;
        this.createdAt = LocalDateTime.now();
    }

    private boolean checkProvider(String provider) {
        try {
            Provider.valueOf(provider);
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * 인증 갱신과 관련된 요청을 받아 인증 정보를 갱신하는 메서드입니다.
     *
     * @param memberUpdateRequest - 인증 정보 갱신 요청 객체
     * @param passwordEncoder
     */
    public String updateAuth(final MemberUpdateRequest memberUpdateRequest, PasswordEncoder passwordEncoder) {
        String updatedUuid = UUID.randomUUID().toString();
        this.uuid = updatedUuid;
        this.name = memberUpdateRequest.getName();
        this.phoneNumber = memberUpdateRequest.getPhoneNumber();
        this.passwordUpdatedAt = getUpdateDate(memberUpdateRequest.getPassword(), passwordEncoder);
        this.password = passwordEncoder.encode(memberUpdateRequest.getPassword());

        return updatedUuid;
    }

    public void deleteAuth(final AuthWithDrawRequest withdrawAt, PasswordEncoder passwordEncoder) {
        if (this.email.equals(withdrawAt.getEmail())
            && passwordEncoder.matches(withdrawAt.getPassword(), this.password)) {

            this.deletedAt = withdrawAt.getWithdrawAt();
        } else {
            throw new AuthNotFoundException();
        }
    }

    /**
     * 패스워드 수정 됬는지 확인하는 메서드입니다.
     *
     * @param updatedPassword - 수정된 비밀번호 입니다.
     * @return LocalDate - 비밀번호가 수정된 날짜를 기점으로 갱신합니다.
     */
    private LocalDate getUpdateDate(final String updatedPassword, PasswordEncoder passwordEncoder) {
        if (isUpdatePassword(updatedPassword, passwordEncoder)) {
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
    private boolean isUpdatePassword(final String updatedPassword, PasswordEncoder passwordEncoder) {
        return Objects.isNull(updatedPassword) || passwordEncoder.matches(updatedPassword, this.password);
    }

    public void updateUuid(final String uuid) {
        this.uuid = uuid;
    }

    public boolean isMember() {
        if (Objects.nonNull(this.deletedAt)) {
            throw new WithdrawMemberException();
        }

        return true;
    }

}
