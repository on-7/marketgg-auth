package com.nhnacademy.marketgg.auth.service;

import com.nhnacademy.marketgg.auth.dto.request.AuthUpdateRequest;
import com.nhnacademy.marketgg.auth.dto.request.AuthWithDrawRequest;
import com.nhnacademy.marketgg.auth.dto.response.MemberResponse;
import com.nhnacademy.marketgg.auth.dto.response.TokenResponse;
import com.nhnacademy.marketgg.auth.exception.UnAuthorizationException;

public interface AuthInfoService {

    MemberResponse findAuthByUuid(final String token) throws UnAuthorizationException;

    TokenResponse update(final String token, final AuthUpdateRequest authUpdateRequest);

    void withdraw(final String token, final AuthWithDrawRequest authWithDrawRequest);

}
