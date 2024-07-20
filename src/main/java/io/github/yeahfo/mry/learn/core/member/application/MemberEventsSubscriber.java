package io.github.yeahfo.mry.learn.core.member.application;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import io.github.yeahfo.mry.learn.core.member.domain.event.MemberCreatedEvent;
import io.github.yeahfo.mry.learn.core.member.domain.event.MemberDomainEvent;
import io.github.yeahfo.mry.learn.core.tenant.domain.event.TenantDomainEvent;
import io.github.yeahfo.mry.learn.core.tenant.domain.event.TenantPlanUpdatedEvent;
import org.springframework.stereotype.Component;

@Component
public class MemberEventsSubscriber {
    public DomainEventHandlers domainEventHandlers( ) {
        return DomainEventHandlersBuilder
                .forAggregateType( MemberDomainEvent.aggregateType )
                .onEvent( MemberCreatedEvent.class, this::memberCreatedEventHandler )
                .andForAggregateType( TenantDomainEvent.aggregateType )
                .onEvent( TenantPlanUpdatedEvent.class, this::tenantPlanUpdatedEventHandler )
                .build( );
    }

    private void tenantPlanUpdatedEventHandler( DomainEventEnvelope< TenantPlanUpdatedEvent > envelope ) {
        System.err.println( "tenantPlanUpdatedEventHandler begin -> " );
        System.err.println( envelope.getAggregateId( ) );
        System.err.println( envelope.getAggregateType( ) );
        System.err.println( envelope.getEvent( ).trigger( ) );
        System.err.println( "<- tenantPlanUpdatedEventHandler end." );
    }

    private void memberCreatedEventHandler( DomainEventEnvelope< MemberCreatedEvent > envelope ) {
        System.err.println( "memberCreatedEventHandler begin -> " );
        System.err.println( envelope.getAggregateId( ) );
        System.err.println( envelope.getAggregateType( ) );
        System.err.println( envelope.getEvent( ).trigger( ) );
        System.err.println( "<- memberCreatedEventHandler end." );
    }
}
