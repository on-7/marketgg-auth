package com.nhnacademy.marketgg.auth.service;

import com.nhnacademy.marketgg.auth.dto.request.AuthUpdateRequest;
import com.nhnacademy.marketgg.auth.dto.request.AuthWithDrawRequest;
import com.nhnacademy.marketgg.auth.dto.response.AuthUpdateResponse;
import com.nhnacademy.marketgg.auth.exception.UnAuthorizationException;

public interface AuthInfoService {
    AuthUpdateResponse update(final String token
            , final AuthUpdateRequest authUpdateRequest) throws UnAuthorizationException;

    void withdraw(final String token
            , final AuthWithDrawRequest authWithDrawRequest) throws UnAuthorizationException;

}
