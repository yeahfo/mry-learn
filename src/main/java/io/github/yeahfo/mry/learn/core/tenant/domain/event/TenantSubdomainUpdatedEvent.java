package io.github.yeahfo.mry.learn.core.tenant.domain.event;

import io.github.yeahfo.mry.learn.core.common.domain.User;

public record TenantSubdomainUpdatedEvent( String oldSubdomainPrefix, String newSubdomainPrefix,
                                           User trigger ) implements TenantDomainEvent {
}
