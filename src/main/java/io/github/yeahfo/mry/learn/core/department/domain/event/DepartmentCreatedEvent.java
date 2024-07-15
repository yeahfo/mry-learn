package io.github.yeahfo.mry.learn.core.department.domain.event;

import io.github.yeahfo.mry.learn.core.common.domain.User;

public record DepartmentCreatedEvent( User user ) implements DepartmentDomainEvent {
}
