package io.github.yeahfo.mry.learn.core.department.domain.event;

import io.eventuate.tram.events.common.DomainEvent;

public interface DepartmentDomainEvent extends DomainEvent {
    String aggregateType = "io.github.yeahfo.mry.learn.core.department.domain.Department";
}
