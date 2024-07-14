package io.github.yeahfo.mry.learn.core.member.application;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import io.github.yeahfo.mry.learn.core.member.domain.MemberCreatedEvent;
import io.github.yeahfo.mry.learn.core.member.domain.MemberDomainEvent;
import org.springframework.stereotype.Component;

@Component
public class MemberEventsSubscriber {
    public DomainEventHandlers domainEventHandlers( ) {
        return DomainEventHandlersBuilder
                .forAggregateType( MemberDomainEvent.aggregateType )
                .onEvent( MemberCreatedEvent.class, this::memberCreatedEventHandler )
                .build( );
    }

    private void memberCreatedEventHandler( DomainEventEnvelope< MemberCreatedEvent > envelope ) {
        System.err.println( "memberCreatedEventHandler begin -> " );
        System.err.println( envelope.getAggregateId( ) );
        System.err.println( envelope.getAggregateType( ) );
        System.err.println( envelope.getEvent( ).name( ) );
        System.err.println( "<- memberCreatedEventHandler end." );
    }

    ;
}
