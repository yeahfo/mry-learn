package io.github.yeahfo.mry.learn.core.member.application;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.github.yeahfo.mry.learn.core.member.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    private final MemberRepository repository;
    private final MemberDomainEventPublisher domainEventPublisher;

    public MemberService( MemberRepository repository, MemberDomainEventPublisher domainEventPublisher ) {
        this.repository = repository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Transactional
    public Member create( final String name ) {
        ResultWithDomainEvents< Member, MemberDomainEvent > resultWithDomainEvents = Member.create( name );
        Member saved = repository.save( resultWithDomainEvents.result );
        domainEventPublisher.publish( saved, resultWithDomainEvents.events );
        return saved;
    }
}
