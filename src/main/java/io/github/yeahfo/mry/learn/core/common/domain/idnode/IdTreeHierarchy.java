package io.github.yeahfo.mry.learn.core.common.domain.idnode;

import jakarta.validation.constraints.NotEmpty;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.collect.Maps.immutableEntry;
import static io.github.yeahfo.mry.learn.core.common.domain.idnode.IdTree.NODE_ID_SEPARATOR;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
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

    public Map< String, String > fullNames( Map< String, String > allNames ) {
        requireNonNull( allNames, "Provided names must not be null." );

        return this.schemas.entrySet( ).stream( ).map( entry -> {
            String names = stream( entry.getValue( ).split( NODE_ID_SEPARATOR ) ).map( groupId -> {
                String name = allNames.get( groupId );
                if ( isBlank( name ) ) {
                    throw new RuntimeException( "No  name found for id[" + groupId + "]." );
                }
                return name;
            } ).collect( joining( NODE_ID_SEPARATOR ) );
            return immutableEntry( entry.getKey( ), names );
        } ).collect( toImmutableMap( Map.Entry::getKey, Map.Entry::getValue ) );
    }
}
