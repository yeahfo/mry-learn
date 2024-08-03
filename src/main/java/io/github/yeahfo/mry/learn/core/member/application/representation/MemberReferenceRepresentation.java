package io.github.yeahfo.mry.learn.core.member.application.representation;

import lombok.Builder;

@Builder
public record MemberReferenceRepresentation( String id,
                                             String showName ) {
}
