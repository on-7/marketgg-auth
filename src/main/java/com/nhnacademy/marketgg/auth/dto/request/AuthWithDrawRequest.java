package com.nhnacademy.marketgg.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class AuthWithDrawRequest {

    private LocalDateTime deletedAt;

}
