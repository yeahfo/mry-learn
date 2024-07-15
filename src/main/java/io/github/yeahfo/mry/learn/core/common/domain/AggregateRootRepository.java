package io.github.yeahfo.mry.learn.core.common.domain;

import java.util.Optional;

public interface AggregateRootRepository< A extends AggregateRoot, ID > {
    A save( A aggregateRoot );

    Optional< A > findById( ID id );
}
