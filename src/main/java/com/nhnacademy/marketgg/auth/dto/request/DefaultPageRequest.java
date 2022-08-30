package com.nhnacademy.marketgg.auth.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Getter
public class DefaultPageRequest {

    private int page;
    private int size;

    public DefaultPageRequest(Integer page) {
        this.page = page;
        this.size = 10;
    }

    public Pageable of(int page) {
        return PageRequest.of(page, 10);
    }

}
