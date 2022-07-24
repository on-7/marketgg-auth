package com.nhnacademy.marketgg.auth.dto.response.common;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SingleResponse<T> extends CommonResponse {

    private final T data;

    public SingleResponse(T data) {
        super(true);
        this.data = data;
    }

}
