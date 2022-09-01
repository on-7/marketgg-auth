package com.nhnacademy.marketgg.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class Payload implements Serializable {

    /**
     * JWT 발행인.
     */
    @JsonProperty("sub")
    private String sub;

    /**
     * 발행인의 권한 목록.
     */
    @JsonProperty("AUTHORITIES")
    private List<String> authorities;

    /**
     * JWT 발행일.
     */
    @JsonProperty("iat")
    private Long iat;

    /**
     * JWT 만료일.
     */
    @JsonProperty("exp")
    private Long exp;

}
