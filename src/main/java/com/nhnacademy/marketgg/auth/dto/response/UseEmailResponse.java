package com.nhnacademy.marketgg.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 이메일 사용을 위한 응답 클래스 입니다.
 */
@RequiredArgsConstructor
@Getter
public class UseEmailResponse {

    private final boolean isUseEmail;

}
