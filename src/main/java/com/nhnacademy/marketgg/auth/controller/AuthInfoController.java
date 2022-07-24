package com.nhnacademy.marketgg.auth.controller;

import static org.springframework.http.HttpStatus.OK;

import com.nhnacademy.marketgg.auth.annotation.Token;
import com.nhnacademy.marketgg.auth.dto.request.AuthUpdateRequest;
import com.nhnacademy.marketgg.auth.dto.request.AuthWithDrawRequest;
import com.nhnacademy.marketgg.auth.dto.response.AuthUpdateResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberResponse;
import com.nhnacademy.marketgg.auth.dto.response.TokenResponse;
import com.nhnacademy.marketgg.auth.dto.response.common.CommonResponse;
import com.nhnacademy.marketgg.auth.dto.response.common.SingleResponse;
import com.nhnacademy.marketgg.auth.exception.UnAuthorizationException;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.service.AuthInfoService;
import com.nhnacademy.marketgg.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 회원 정보 관련 정보 요청을 처리하는 클래스입니다.
 *
 * @version 1.0.0
 */
@RestController
@RequestMapping("/auth/info")
@RequiredArgsConstructor
public class AuthInfoController {

    private final AuthInfoService authInfoService;

    private final AuthService authService;

    /**
     * 회원정보 수정을 위한 컨트롤러 메서드 입니다.
     *
     * @param token             - JWT
     * @param authUpdateRequest - 수정할 회원 정보를 담고있는 객체 입니다.
     * @return - 상태코드를 리턴합니다.
     */
    @PutMapping
    public ResponseEntity<Void> update(@Token String token,
                                       @RequestBody final AuthUpdateRequest authUpdateRequest) {

        TokenResponse update
            = authInfoService.update(token, authUpdateRequest);

        authService.logout(token);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(update.getJwt());
        httpHeaders.set(TokenUtils.JWT_EXPIRE, update.getExpiredDate().toString());

        return ResponseEntity.status(OK)
                             .headers(httpHeaders)
                             .build();
    }

    /**
     * 회원정보 삭제를 위한 컨트롤러 메서드 입니다.
     *
     * @param token - JWT
     * @return - AuthWithdrawResponse Auth 서버와 Shop 서버간의 통신에서 오는 시간 격차를 줄이기 위한 객체 입니다.
     */
    @DeleteMapping
    public ResponseEntity<AuthUpdateResponse> withdraw(
        @Token String token,
        @RequestBody final AuthWithDrawRequest authWithDrawRequest) {

        authInfoService.withdraw(token, authWithDrawRequest);

        return ResponseEntity.status(OK)
                             .build();
    }

    /**
     * JWT 토큰을 이용하여 사용자 정보를 응답합니다.
     *
     * @param token - JWT
     * @return - 사용자 정보
     * @throws UnAuthorizationException - JWT 를 통해 인증할 수 없는 사용자일 경우 발생하는 예외
     */
    @GetMapping
    public ResponseEntity<? extends CommonResponse> getAuthInfo(@Token String token)
        throws UnAuthorizationException {

        MemberResponse auth = authInfoService.findAuthByUuid(token);
        SingleResponse<MemberResponse> memberResponseSingleResponse =
            new SingleResponse<>(auth);

        return ResponseEntity.status(HttpStatus.OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(memberResponseSingleResponse);
    }

}
