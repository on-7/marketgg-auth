package com.nhnacademy.marketgg.auth.service;

import com.nhnacademy.marketgg.auth.dto.request.UpdateRequest;
import com.nhnacademy.marketgg.auth.dto.response.MemberResponse;
import com.nhnacademy.marketgg.auth.exception.UnAuthorizationException;

public interface AuthInfoService {

    MemberResponse findAuthByUuid(final String token) throws UnAuthorizationException;

    void update(UpdateRequest updateRequest);

}
