package io.github.yeahfo.mry.learn.core.common.domain.idnode;

import jakarta.validation.constraints.NotEmpty;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static io.github.yeahfo.mry.learn.core.common.domain.idnode.IdTree.NODE_ID_SEPARATOR;
import static org.apache.commons.lang3.StringUtils.isBlank;

public record IdTreeHierarchy( @NotEmpty( message = "Hierarchy must not be null." ) Map< String, String > schemas ) {

    public Set< String > directChildIdsUnder( String parentId ) {
        if ( isBlank( parentId ) ) {
            return this.schemas.values( ).stream( )
                    .filter( value -> !value.contains( NODE_ID_SEPARATOR ) )
                    .collect( toImmutableSet( ) );
        }

        return this.schemas.values( ).stream( )
                .filter( value -> value.contains( parentId + NODE_ID_SEPARATOR ) )
                .map( value -> {
                    String[] split = value.split( parentId + NODE_ID_SEPARATOR );
                    return split[ 1 ].split( NODE_ID_SEPARATOR )[ 0 ];
                } ).collect( toImmutableSet( ) );
    }
}
