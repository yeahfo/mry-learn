package io.github.yeahfo.mry.learn.core.member.domain.event;

import io.eventuate.tram.events.aggregates.AbstractAggregateDomainEventPublisher;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.github.yeahfo.mry.learn.core.member.domain.Member;
import org.springframework.stereotype.Component;


@Component
public class MemberDomainEventPublisher extends AbstractAggregateDomainEventPublisher< Member, MemberDomainEvent > {
    public MemberDomainEventPublisher( DomainEventPublisher domainEventPublisher ) {
        super( domainEventPublisher, Member.class, Member::identifier );
    }
}
