package com.nhnacademy.marketgg.auth.dto.request.signup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 중복 이메일처리와 해당 이메일이 추천인 이메일인지를 확인하기 위한 클래스입니다.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EmailRequest {

    private String email;
    private boolean isReferrer;

}
