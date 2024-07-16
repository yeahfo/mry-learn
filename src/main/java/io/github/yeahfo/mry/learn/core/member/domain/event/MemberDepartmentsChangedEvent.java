package io.github.yeahfo.mry.learn.core.member.domain.event;

import io.github.yeahfo.mry.learn.core.common.domain.User;

import java.util.Set;

public record MemberDepartmentsChangedEvent( Set< String > removedDepartmentIds,
                                             Set< String > addedDepartmentIds,
                                             User trigger ) implements MemberDomainEvent {
}
