package io.github.yeahfo.mry.learn.core.common.domain.idnode;

import io.github.yeahfo.mry.learn.core.common.domain.idnode.exception.IdNodeLevelOverflowException;
import io.github.yeahfo.mry.learn.core.common.domain.idnode.exception.NodeIdFormatException;
import io.github.yeahfo.mry.learn.core.common.domain.idnode.validation.NodeId;
import io.github.yeahfo.mry.learn.core.common.validation.collection.NoNullElement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Map.copyOf;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.countMatches;

public record IdTree( @Valid
                      @NotNull
                      @NoNullElement
                      @Size( max = 1000 )
                      List< IdNode > nodes ) {
    public static final String NODE_ID_SEPARATOR = "/";

    public IdTreeHierarchy buildHierarchy( int maxAllowedLevel ) {
        return new IdTreeHierarchy( this.nodes.stream( ).map( idNode -> idNode.buildHierarchy( maxAllowedLevel ) )
                .flatMap( map -> map.entrySet( ).stream( ) )
                .collect( toImmutableMap( Map.Entry::getKey, Map.Entry::getValue ) ) );
    }

    @Getter
    @EqualsAndHashCode
    @NoArgsConstructor( access = PRIVATE )
    static class IdNode {

        @NodeId
        @NotBlank
        @Size( max = 50 )
        private String id;

        @Valid
        @NotNull
        @NoNullElement
        @Size( max = 1000 )
        private List< IdNode > children;

        private IdNode( String id ) {
            if ( id.contains( NODE_ID_SEPARATOR ) ) {
                throw new NodeIdFormatException( "Node ID must not contain " + NODE_ID_SEPARATOR + "." );
            }

            this.id = id;
            this.children = new ArrayList<>( 0 );
        }

        private Map< String, String > buildHierarchy( int maxAllowedLevel ) {
            Map< String, String > result = new HashMap<>( );
            result.put( id, id );

            if ( isEmpty( children ) ) {
                return result;
            }

            children.forEach( idNode -> {
                Map< String, String > hierarchy = idNode.buildHierarchy( maxAllowedLevel );
                for ( Map.Entry< String, String > entry : hierarchy.entrySet( ) ) {
                    String value = id + NODE_ID_SEPARATOR + entry.getValue( );
                    int level = countMatches( value, NODE_ID_SEPARATOR ) + 1;
                    if ( level > maxAllowedLevel ) {
                        throw new IdNodeLevelOverflowException( "Max allowed level is " + maxAllowedLevel + " but actual is " + level + "." );
                    }
                    result.put( entry.getKey( ), value );
                }
            } );

            return copyOf( result );
        }

        private IdNode nodeById( String id ) {
            if ( Objects.equals( this.id, id ) ) {
                return this;
            }

            for ( IdNode child : children ) {
                IdNode idNode = child.nodeById( id );
                if ( idNode != null ) {
                    return idNode;
                }
            }

            return null;
        }

        private void addChild( String nodeId ) {
            this.children.add( 0, new IdNode( nodeId ) );
        }

        private boolean removeChildren( String nodeId ) {
            boolean result = this.children.removeIf( idNode -> Objects.equals( nodeId, idNode.id ) );
            if ( result ) {
                return true;
            }

            for ( IdNode child : children ) {
                boolean childrenResult = child.removeChildren( nodeId );
                if ( childrenResult ) {
                    return true;
                }
            }

            return false;
        }

        private IdNode map( Map< String, String > idMap ) {
            IdNode mappedNode = new IdNode( idMap.get( this.id ) );
            if ( isEmpty( children ) ) {
                return mappedNode;
            }

            children.forEach( idNode -> mappedNode.children.add( idNode.map( idMap ) ) );
            return mappedNode;
        }
    }
}
