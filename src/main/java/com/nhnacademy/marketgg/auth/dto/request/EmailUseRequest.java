package com.nhnacademy.marketgg.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이메일 사용을 위해 이메일을 확인하기 위한 요청 클래스 입니다.
 */
@NoArgsConstructor
@Getter
public class EmailUseRequest {

    private String email;

}
