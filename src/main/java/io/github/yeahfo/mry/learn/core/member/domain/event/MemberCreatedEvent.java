package io.github.yeahfo.mry.learn.core.member.domain.event;

import io.github.yeahfo.mry.learn.core.common.domain.User;

public record MemberCreatedEvent( User trigger ) implements MemberDomainEvent {
}
