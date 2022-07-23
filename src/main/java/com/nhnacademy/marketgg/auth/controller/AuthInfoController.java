package com.nhnacademy.marketgg.auth.controller;

import com.nhnacademy.marketgg.auth.dto.request.UpdateRequest;
import com.nhnacademy.marketgg.auth.service.AuthInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
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

}
