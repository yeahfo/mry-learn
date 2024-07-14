package io.github.yeahfo.mry.learn.core.member.domain;

import io.github.yeahfo.mry.learn.core.common.utils.SnowflakeIdGenerator;

public class Disruptor {
    private Long id;
    private String name;

    public static Disruptor of( final String name ) {
        Disruptor disruptor = new Disruptor( );
        disruptor.id = SnowflakeIdGenerator.newSnowflakeId( );
        disruptor.name = name;
        return disruptor;
    }
}
