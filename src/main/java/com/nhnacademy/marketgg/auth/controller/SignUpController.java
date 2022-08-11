package com.nhnacademy.marketgg.auth.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.nhnacademy.marketgg.auth.dto.request.EmailRequest;
import com.nhnacademy.marketgg.auth.dto.request.EmailUseRequest;
import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import com.nhnacademy.marketgg.auth.dto.response.ExistEmailResponse;
import com.nhnacademy.marketgg.auth.dto.response.SignUpResponse;
import com.nhnacademy.marketgg.auth.dto.response.UseEmailResponse;
import com.nhnacademy.marketgg.auth.service.SignUpService;
import javax.management.relation.RoleNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 관련 요청을 처리하는 Controller 입니다.
 *
 * @version 1.0.0
 * @author 김훈민
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpService signUpService;

    /**
     * 회원가입 요청을 받아 회원가입을 진행합니다.
     *
     * @param signUpRequest - 회원가입에 필요한 요청 정보 객체
     * @return 회원가입 성공/실패 여부가 담긴 ResponseEntity
     * @throws RoleNotFoundException - 역할을 부여받지 않거나, 읽을 수 없는 경우 예외 발생
     */
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> doSignup(@RequestBody final SignUpRequest signUpRequest)
        throws RoleNotFoundException {

        SignUpResponse signup = signUpService.signup(signUpRequest);

        return ResponseEntity.status(CREATED)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(signup);
    }

    /**
     * 요청한 이메일이 중복되는지 확인합니다.
     *
     * @param emailRequest - 이메일 로그인 요청 정보 객체
     * @return 이메일로 로그인 요청 존재하는 이메일인지 성공/실패 여부가 담긴 ResponseEntity
     */
    @PostMapping("/check/email")
    public ResponseEntity<ExistEmailResponse> checkEmail(
        @RequestBody final EmailRequest emailRequest) {
        return ResponseEntity.status(OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(signUpService.checkEmail(emailRequest));
    }

    /**
     * 요청한 이메일이 중복되는지 확인합니다.
     *
     * @param emailUseRequest - 이메일 로그인 요청 정보 객체
     * @return 이메일로 로그인 요청 사용할 수 있는 이메일인지 성공/실패 여부가 담긴 ResponseEntity
     */
    @PostMapping("/use/email")
    public ResponseEntity<UseEmailResponse> useEmail(
        @RequestBody final EmailUseRequest emailUseRequest) {
        return ResponseEntity.status(OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(signUpService.useEmail(emailUseRequest));
    }

}
