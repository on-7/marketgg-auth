package com.nhnacademy.marketgg.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 존재하는 이메일과 추천인 이메일의 존재를 확인하기 위한 클래스입니다.
 */
@NoArgsConstructor
@Getter
public class EmailRequest {

    private String email;
    private boolean isReferrer;

}
