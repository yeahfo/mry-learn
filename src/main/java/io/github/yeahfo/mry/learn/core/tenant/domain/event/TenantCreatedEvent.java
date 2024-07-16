package io.github.yeahfo.mry.learn.core.tenant.domain.event;

import io.github.yeahfo.mry.learn.core.common.domain.User;

import java.util.List;

public record TenantCreatedEvent( User trigger ) implements TenantDomainEvent {
}
