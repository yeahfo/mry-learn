package io.github.yeahfo.mry.learn.core.member.domain;

import lombok.Builder;

@Builder
public record IdentityCard( String number,
                            String name ) {
}
