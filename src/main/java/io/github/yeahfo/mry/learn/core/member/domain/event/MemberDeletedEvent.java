package io.github.yeahfo.mry.learn.core.member.domain.event;

import io.github.yeahfo.mry.learn.core.common.domain.User;

public record MemberDeletedEvent( User trigger ) implements MemberDomainEvent {
}
