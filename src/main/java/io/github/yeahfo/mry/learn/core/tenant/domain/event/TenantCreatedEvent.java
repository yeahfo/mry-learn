package io.github.yeahfo.mry.learn.core.tenant.domain.event;

import io.github.yeahfo.mry.learn.core.common.domain.User;

public record TenantCreatedEvent( User trigger ) implements TenantDomainEvent {
}
