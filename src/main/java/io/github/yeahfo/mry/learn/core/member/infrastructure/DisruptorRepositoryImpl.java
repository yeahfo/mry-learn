package io.github.yeahfo.mry.learn.core.member.infrastructure;

import io.github.yeahfo.mry.learn.core.member.domain.Disruptor;
import io.github.yeahfo.mry.learn.core.member.domain.DisruptorRepository;
import org.springframework.stereotype.Repository;

@Repository
public class DisruptorRepositoryImpl implements DisruptorRepository {

    @Override
    public Disruptor save( Disruptor disruptor ) {
        throw new UnsupportedOperationException();
    }
}
