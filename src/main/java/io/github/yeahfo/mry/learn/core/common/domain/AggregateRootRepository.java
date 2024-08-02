package io.github.yeahfo.mry.learn.core.common.domain;

import io.github.yeahfo.mry.learn.core.common.exception.MryException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.AR_NOT_FOUND;
import static io.github.yeahfo.mry.learn.core.common.utils.CommonUtils.requireNonBlank;
import static io.github.yeahfo.mry.learn.core.common.utils.MapUtils.mapOf;
import static java.util.Objects.requireNonNull;

public interface AggregateRootRepository< A extends AggregateRoot, ID > {
    @SuppressWarnings( "rawtypes" )
    Map< String, Class > classMapper = new HashMap<>( );

    A save( A aggregateRoot );

    Optional< A > findById( ID id );

    default A findByIdExacted( ID id ) {
        requireNonBlank( String.valueOf( id ), "AR ID must not be blank." );
        return findById( id ).orElseThrow( ( ) -> new MryException( AR_NOT_FOUND, "未找到资源。", mapOf( "type", getType( ).getSimpleName( ), "id", id ) ) );
    }

    default A findByIdAndCheckTenantShip( ID id, User user ) throws MryException {
        A aggregateRoot = findById( id ).orElse( null );
        checkTenantShip( aggregateRoot, user );
        return aggregateRoot;
    }

    @SuppressWarnings( "rawtypes" )
    default Class getType( ) {
        String className = getClass( ).getSimpleName( );

        if ( !classMapper.containsKey( className ) ) {
            Type genericSuperclass = getClass( ).getGenericSuperclass( );
            Type[] actualTypeArguments = ( ( ParameterizedType ) genericSuperclass ).getActualTypeArguments( );
            classMapper.put( className, ( Class ) actualTypeArguments[ 0 ] );
        }
        return classMapper.get( className );
    }

    default void checkTenantShip( AggregateRoot aggregateRoot, User user ) {
        requireNonNull( aggregateRoot, "AR must not be null." );
        requireNonNull( user, "User must not be null." );

        if ( !Objects.equals( aggregateRoot.tenantId( ), user.tenantId( ) ) ) {
            throw new MryException( AR_NOT_FOUND, "未找到资源。", mapOf( "id", aggregateRoot.identifier( ), "tenantId", aggregateRoot.tenantId( ) ) );
        }
    }
}
