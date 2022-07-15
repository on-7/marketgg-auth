package com.nhnacademy.marketgg.auth.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GoogleProfile {

    private String email;
    private String name;
    private String gender;
    private String address;
    private String birthday;

}
