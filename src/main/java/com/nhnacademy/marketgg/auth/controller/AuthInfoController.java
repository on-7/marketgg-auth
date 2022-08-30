package com.nhnacademy.marketgg.auth.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nhnacademy.marketgg.auth.annotation.Token;
import com.nhnacademy.marketgg.auth.dto.request.AuthWithDrawRequest;
import com.nhnacademy.marketgg.auth.dto.request.MemberInfoRequest;
import com.nhnacademy.marketgg.auth.dto.request.MemberUpdateRequest;
import com.nhnacademy.marketgg.auth.dto.response.AdminMemberResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberInfoResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberNameResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberResponse;
import com.nhnacademy.marketgg.auth.dto.response.UuidTokenResponse;
import com.nhnacademy.marketgg.auth.dto.response.common.AuthResult;
import com.nhnacademy.marketgg.auth.dto.response.common.PageEntity;
import com.nhnacademy.marketgg.auth.exception.UnAuthorizationException;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.service.AuthInfoService;
import com.nhnacademy.marketgg.auth.service.AuthService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 회원 정보 관련 정보 요청을 처리하는 클래스입니다.
 *
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/members/info")
@RequiredArgsConstructor
public class AuthInfoController {

    private final AuthInfoService authInfoService;
    private final AuthService authService;

    /**
     * <<<<<<< HEAD
     * 회원정보 수정을 위한 컨트롤러 메서드 입니다.
     *
     * @param token               - JWT
     * @param memberUpdateRequest - 수정할 회원 정보를 담고있는 객체 입니다.
     * @return - 상태코드를 리턴합니다.
     * @author 김훈민
     */
    @PutMapping
    public ResponseEntity<AuthResult<UuidTokenResponse>> update(@Token String token,
                                                                @Valid @RequestBody
                                                                final MemberUpdateRequest memberUpdateRequest) {

        UuidTokenResponse update = authInfoService.update(token, memberUpdateRequest);

        authService.logout(token);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(update.getJwt());
        httpHeaders.set(TokenUtils.JWT_EXPIRE, update.getExpiredDate().toString());

        return ResponseEntity.status(OK)
                             .headers(httpHeaders)
                             .body(AuthResult.success(update));
    }

    /**
     * 회원정보 삭제를 위한 컨트롤러 메서드 입니다.
     *
     * @param token      - JWT
     * @param withdrawAt - Shop 에서 보낸 삭제 시간 입니다.
     * @return - 상태코드를 리턴합니다.
     * @author 김훈민
     */
    @DeleteMapping
    public ResponseEntity<AuthResult<String>> withdraw(@Token String token,
                                                       @Valid @RequestBody final AuthWithDrawRequest withdrawAt) {

        authInfoService.withdraw(token, withdrawAt);
        authService.logout(token);

        return ResponseEntity.status(OK)
                             .contentType(APPLICATION_JSON)
                             .body(AuthResult.success("Withdraw Success"));
    }

    /**
     * JWT 토큰을 이용하여 사용자 정보를 응답합니다.
     *
     * @param memberInfoRequest - 요청하려는 사용자 정보
     * @return - 사용자 정보
     * @author 윤동열
     */
    @PostMapping("/person")
    public ResponseEntity<AuthResult<MemberInfoResponse>> getMemberInfo(
        @RequestBody MemberInfoRequest memberInfoRequest) {
        MemberInfoResponse data = authInfoService.findMemberInfoByUuid(memberInfoRequest.getUuid());

        return ResponseEntity.status(OK)
                             .contentType(APPLICATION_JSON)
                             .body(AuthResult.success(data));
    }

    /**
     * UUID 목록에 알맞은 회원 목록을 조회합니다.
     *
     * @param uuids - 필요한 회원의 UUID
     * @return 회원 목록
     * @author 윤동열
     */
    @PostMapping("/names")
    public ResponseEntity<AuthResult<List<MemberNameResponse>>> getMemberList(@RequestBody List<String> uuids) {
        List<MemberNameResponse> data = authInfoService.findMemberNameList(uuids);

        return ResponseEntity.status(OK)
                             .contentType(APPLICATION_JSON)
                             .body(AuthResult.success(data));
    }

    /**
     * JWT 토큰을 이용하여 사용자 정보를 응답합니다.
     *
     * @param token - JWT
     * @return - 사용자 정보
     * @throws UnAuthorizationException - JWT 를 통해 인증할 수 없는 사용자일 경우 발생하는 예외
     * @author 윤동열
     */
    @GetMapping
    public ResponseEntity<AuthResult<MemberResponse>> getAuthInfo(@Token String token) throws UnAuthorizationException {
        MemberResponse data = authInfoService.findAuthByUuid(token);
        log.info("MemberResponse = {}", data);

        return ResponseEntity.status(OK)
                             .contentType(APPLICATION_JSON)
                             .body(AuthResult.success(data));
    }

    /**
     * 사용자 목록을 조회합니다.
     *
     * @param token - JWT
     * @param page  - 페이지
     * @return 회원 정보 목록
     */
    @GetMapping("/list")
    public ResponseEntity<AuthResult<PageEntity<AdminMemberResponse>>> retrieveMembers(
        @Token String token, @RequestParam(value = "page", defaultValue = "0") final Integer page)
        throws JsonProcessingException {

        if (!authInfoService.isAdmin(token)) {
            throw new UnAuthorizationException();
        }

        PageEntity<AdminMemberResponse> data = authInfoService.findAdminMembers(PageRequest.of(page, 10));

        return ResponseEntity.status(OK)
                             .contentType(APPLICATION_JSON)
                             .body(AuthResult.success(data));
    }

}
