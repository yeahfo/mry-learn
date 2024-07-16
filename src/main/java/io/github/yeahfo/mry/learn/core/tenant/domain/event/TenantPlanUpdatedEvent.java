package io.github.yeahfo.mry.learn.core.tenant.domain.event;

import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.plan.domain.PlanType;

import java.util.List;

public record TenantPlanUpdatedEvent( PlanType planType, User trigger ) implements TenantDomainEvent {
}
