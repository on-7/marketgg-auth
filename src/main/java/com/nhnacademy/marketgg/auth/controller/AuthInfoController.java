package com.nhnacademy.marketgg.auth.controller;

import static org.springframework.http.HttpStatus.OK;

import com.nhnacademy.marketgg.auth.dto.request.UpdateRequest;
import com.nhnacademy.marketgg.auth.dto.response.MemberResponse;
import com.nhnacademy.marketgg.auth.dto.response.common.CommonResponse;
import com.nhnacademy.marketgg.auth.dto.response.common.SingleResponse;
import com.nhnacademy.marketgg.auth.exception.UnAuthorizationException;
import com.nhnacademy.marketgg.auth.service.AuthInfoService;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/info")
@RequiredArgsConstructor
public class AuthInfoController {

    private final AuthInfoService authInfoService;

    /**
     * 회원정보 수정을 위한 컨트롤러 메서드 입니다.
     *
     * @param updateRequest - 수정할 회원 정보를 담고있는 객체 입니다.
     * @return - 상태코드를 리턴합니다.
     */
    @PutMapping
    public ResponseEntity<Void> update(@RequestBody final UpdateRequest updateRequest) {
        authInfoService.update(updateRequest);
        return ResponseEntity.status(OK)
                             .build();
    }

    @GetMapping
    public ResponseEntity<? extends CommonResponse> getAuthInfo(HttpServletRequest request)
        throws UnAuthorizationException {

        String jwt = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                             .orElseThrow(UnAuthorizationException::new);

        MemberResponse auth = authInfoService.findAuthByUuid(jwt);
        SingleResponse<MemberResponse> memberResponseSingleResponse =
            new SingleResponse<>(auth);

        return ResponseEntity.status(HttpStatus.OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(memberResponseSingleResponse);
    }

}
