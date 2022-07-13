package com.nhnacademy.marketgg.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class GoogleProfile {

    private String email;
    private String name;
    private String gender;
    private String address;
    private String birthday;

}