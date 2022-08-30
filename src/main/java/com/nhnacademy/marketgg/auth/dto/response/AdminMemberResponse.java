package com.nhnacademy.marketgg.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nhnacademy.marketgg.auth.constant.Roles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public class AdminMemberResponse {

    private final Long id;
    private final String uuid;
    private final String email;
    private final String name;
    private final String phoneNumber;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;

    @Setter
    private List<Roles> roles = new ArrayList<>();

}
