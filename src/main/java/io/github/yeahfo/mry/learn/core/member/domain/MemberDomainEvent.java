package io.github.yeahfo.mry.learn.core.member.domain;

import io.eventuate.tram.events.common.DomainEvent;

public interface MemberDomainEvent extends DomainEvent {
    String aggregateType = "io.github.yeahfo.mry.learn.core.member.domain.Member";
}
