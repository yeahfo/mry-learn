package io.github.yeahfo.mry.learn.core.member.domain.event;

import io.github.yeahfo.mry.learn.core.common.domain.DomainEvent;

public interface MemberDomainEvent extends DomainEvent {
    String aggregateType = "io.github.yeahfo.mry.learn.core.member.domain.Member";
}
