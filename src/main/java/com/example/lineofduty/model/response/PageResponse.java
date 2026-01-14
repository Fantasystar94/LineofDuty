package com.example.lineofduty.model.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class PageResponse<T> {

    private final List<T> content;
    private final long totalElements;
    private final long totalPages;
    private final int size;
    private final int number;

    public static <T> PageResponse<T> from(Page<T> response) {
        return new PageResponse<>(
            response.getContent(),
            response.getTotalElements(),
            response.getTotalPages(),
            response.getSize(),
            response.getNumber());
    }
}
