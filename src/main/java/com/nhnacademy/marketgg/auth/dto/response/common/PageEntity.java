package com.nhnacademy.marketgg.auth.dto.response.common;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PageEntity<T> {

    private final Integer pageNumber;

    private final Integer pageSize;

    private final Integer totalPages; // 총 페이지 수

    private final List<T> data;

}
