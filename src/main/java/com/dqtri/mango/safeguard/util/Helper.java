package com.dqtri.mango.safeguard.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;


public class Helper {
    public static <T> Page<T> createPagination(List<T> content, Page<?> page) {
        return new PageImpl<>(content,
                PageRequest.of(page.getNumber(), page.getSize(), page.getSort()),
                page.getTotalElements());
    }
}
