package com.nhnacademy.marketgg.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원 정보를 저장하기 위한 클래스
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    String username;
    String password;

}
