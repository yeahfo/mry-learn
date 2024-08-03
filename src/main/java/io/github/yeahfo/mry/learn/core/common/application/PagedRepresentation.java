package io.github.yeahfo.mry.learn.core.common.application;

import io.github.yeahfo.mry.learn.core.common.utils.Pagination;
import lombok.Builder;

import java.util.List;

@Builder
public record PagedRepresentation< T >( int page,
                                        int size,
                                        long total,
                                        List< T > representations ) {

    public static <T> PagedRepresentation< T > pagedList( Pagination pagination, int count, List<T> representations) {
        return PagedRepresentation.<T>builder()
                .total(count)
                .size(pagination.size())
                .page(pagination.page())
                .representations(representations)
                .build();
    }
}
