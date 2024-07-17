package io.github.yeahfo.mry.learn.core.common.utils;

import lombok.Builder;

@Builder
public record IdentifierRepresentation( String id ) {
    public static IdentifierRepresentation identifier( String id ) {
        return new IdentifierRepresentation( id );
    }

    @Override
    public String toString( ) {
        return id;
    }
}
