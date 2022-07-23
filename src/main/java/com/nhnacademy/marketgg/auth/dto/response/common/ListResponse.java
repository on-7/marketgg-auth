package com.nhnacademy.marketgg.auth.dto.response.common;

import java.util.List;
import lombok.Getter;

@Getter
public class ListResponse<T> extends CommonResponse {

    private final List<T> data;

    public ListResponse(boolean success, List<T> data) {
        super(success);
        this.data = data;
    }

}
