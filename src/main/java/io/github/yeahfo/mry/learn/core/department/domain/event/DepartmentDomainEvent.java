package io.github.yeahfo.mry.learn.core.department.domain.event;

import io.github.yeahfo.mry.learn.core.common.domain.DomainEvent;

public interface DepartmentDomainEvent extends DomainEvent {
    String aggregateType = "io.github.yeahfo.mry.learn.core.department.domain.Department";
}
