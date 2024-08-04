package io.github.yeahfo.mry.learn.core.tenant.domain.event;

import io.eventuate.tram.events.aggregates.AbstractAggregateDomainEventPublisher;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.github.yeahfo.mry.learn.core.tenant.domain.Tenant;
import org.springframework.stereotype.Component;

@Component
public class TenantDomainEventPublisher extends AbstractAggregateDomainEventPublisher< Tenant, TenantDomainEvent > {
    protected TenantDomainEventPublisher( DomainEventPublisher eventPublisher ) {
        super( eventPublisher, Tenant.class, Tenant::identifier );
    }
}
