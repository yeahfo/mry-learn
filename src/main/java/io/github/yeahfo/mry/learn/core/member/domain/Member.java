package io.github.yeahfo.mry.learn.core.member.domain;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.github.yeahfo.mry.learn.core.common.utils.SnowflakeIdGenerator;


public class Member {
    private Long id;
    private String name;

    public static ResultWithDomainEvents< Member, MemberDomainEvent > create( String name ) {
        Member member = new Member( );
        member.id = SnowflakeIdGenerator.newSnowflakeId( );
        member.name = name;
        return new ResultWithDomainEvents<>( member, new MemberCreatedEvent( name ) );
    }

    public Long id( ) {
        return id;
    }

    public String name( ) {
        return name;
    }
}
