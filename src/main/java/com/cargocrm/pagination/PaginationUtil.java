package com.cargocrm.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class PaginationUtil {
    public static Pageable createPageable(int page, int limit, String sortBy, String sortDir,
                                          List<String> allowedFields, String defaultField) {
        int validPage = validatePage(page);
        int validLimit = validateLimit(limit);

        String safeSortBy = (allowedFields != null && allowedFields.contains(sortBy)) ? sortBy : defaultField;

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, safeSortBy).and(Sort.by("id"));

        return PageRequest.of(validPage, validLimit, sort);
    }

    private static int validatePage(int page) {
        if (page < 0) {
            return 0;
        }
        return page;
    }

    private static int validateLimit(int limit) {
        if (limit < 1) {
            return 1;
        }
        if (limit > 100) {
            return 100;
        }
        return limit;
    }
}
