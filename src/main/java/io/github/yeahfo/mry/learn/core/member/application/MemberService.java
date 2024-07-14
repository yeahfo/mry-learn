package io.github.yeahfo.mry.learn.core.member.application;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.github.yeahfo.mry.learn.core.member.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    private final MemberRepository repository;
    private final DisruptorRepository disruptorRepository;
    private final MemberDomainEventPublisher domainEventPublisher;

    public MemberService( MemberRepository repository, DisruptorRepository disruptorRepository, MemberDomainEventPublisher domainEventPublisher ) {
        this.repository = repository;
        this.disruptorRepository = disruptorRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Transactional
    public Member create( final String name ) {
        ResultWithDomainEvents< Member, MemberDomainEvent > resultWithDomainEvents = Member.create( name );
        Member saved = repository.save( resultWithDomainEvents.result );
        disruptorRepository.save( Disruptor.of( name ) );
        domainEventPublisher.publish( saved, resultWithDomainEvents.events );
        return saved;
    }
}
