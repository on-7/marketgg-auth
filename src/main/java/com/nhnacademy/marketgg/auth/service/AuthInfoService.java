package com.nhnacademy.marketgg.auth.service;

import com.nhnacademy.marketgg.auth.dto.response.MemberResponse;
import com.nhnacademy.marketgg.auth.exception.UnAuthorizationException;

public interface AuthInfoService {

    MemberResponse findAuthByUuid(String token) throws UnAuthorizationException;

}
