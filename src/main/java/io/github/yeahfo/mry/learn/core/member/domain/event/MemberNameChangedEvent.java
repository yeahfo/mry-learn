package io.github.yeahfo.mry.learn.core.member.domain.event;

import io.github.yeahfo.mry.learn.core.common.domain.User;

public record MemberNameChangedEvent( String newName,
                                      User trigger ) implements MemberDomainEvent {
}

