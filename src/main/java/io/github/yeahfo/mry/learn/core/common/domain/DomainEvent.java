package io.github.yeahfo.mry.learn.core.common.domain;

public interface DomainEvent extends io.eventuate.tram.events.common.DomainEvent {

    User trigger( );
}
