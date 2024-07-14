package io.github.yeahfo.mry.learn.core.member.domain;

import io.eventuate.tram.events.aggregates.AbstractAggregateDomainEventPublisher;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import org.springframework.stereotype.Component;


@Component
public class MemberDomainEventPublisher extends AbstractAggregateDomainEventPublisher< Member, MemberDomainEvent > {
    public MemberDomainEventPublisher( DomainEventPublisher domainEventPublisher ) {
        super( domainEventPublisher, Member.class, Member::id );
    }
}
