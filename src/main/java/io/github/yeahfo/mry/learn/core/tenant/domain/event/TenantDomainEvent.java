package io.github.yeahfo.mry.learn.core.tenant.domain.event;

import io.github.yeahfo.mry.learn.core.common.domain.DomainEvent;

public interface TenantDomainEvent extends DomainEvent {
    String aggregateType = "io.github.yeahfo.mry.learn.core.tenant.domain.Tenant";
}
