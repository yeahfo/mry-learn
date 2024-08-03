package io.github.yeahfo.mry.learn.core.login.application;

import lombok.Builder;

@Builder
public record JwtTokenRepresentation( String token ) {
}
