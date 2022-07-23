package com.nhnacademy.marketgg.auth.controller;

import com.nhnacademy.marketgg.auth.dto.response.common.CommonResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberResponse;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/info")
@RequiredArgsConstructor
public class AuthInfoController {

    private final AuthInfoService authInfoService;

    @GetMapping
    public ResponseEntity<? extends CommonResponse> getAuthInfo(HttpServletRequest request)
        throws UnAuthorizationException {

        String jwt = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                                .orElseThrow(UnAuthorizationException::new);

        MemberResponse auth = authInfoService.findAuthByUuid(jwt);
        SingleResponse<MemberResponse> memberResponseSingleResponse =
            new SingleResponse<>(auth);

        System.out.println((memberResponseSingleResponse));

        return ResponseEntity.status(HttpStatus.OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(memberResponseSingleResponse);
    }

}
