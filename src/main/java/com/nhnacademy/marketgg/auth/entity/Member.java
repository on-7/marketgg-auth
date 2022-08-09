package com.nhnacademy.marketgg.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO 2: 회원 정보를 저장하는 Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    String username;
    String password;

}
