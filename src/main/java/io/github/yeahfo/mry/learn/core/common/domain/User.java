package io.github.yeahfo.mry.learn.core.common.domain;

import io.github.yeahfo.mry.learn.core.common.exception.MryException;

import java.util.Objects;

import static io.github.yeahfo.mry.learn.core.common.domain.Role.*;
import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.WRONG_TENANT;
import static io.github.yeahfo.mry.learn.core.common.exception.MryException.accessDeniedException;
import static io.github.yeahfo.mry.learn.core.common.exception.MryException.authenticationException;
import static io.github.yeahfo.mry.learn.core.common.utils.CommonUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

public record User( String memberId,
                    String name,
                    String tenantId,
                    Role role ) {
    public static final User NOUSER = new User( null, null, null, null );
    public static final User ANONYMOUS_USER = NOUSER;

    public static User humanUser( String memberId, String name, String tenantId, Role role ) {
        requireNonNull( memberId, "MemberId must not be blank." );
        requireNonBlank( name, "Name must not be blank." );
        requireNonNull( tenantId, "TenantId must not be blank." );
        requireNonNull( role, "Role must not be null." );

        if ( role == ROBOT ) {
            throw new IllegalStateException( "Human user should not have ROBOT role." );
        }

        return new User( memberId, name, tenantId, role );
    }

    public boolean isHumanUser( ) {
        if ( !internalIsLoggedIn( ) ) {
            return false;
        }

        return internalIsHumanUser( );
    }

    public boolean isLoggedIn( ) {
        return internalIsLoggedIn( );
    }

    private boolean internalIsLoggedIn( ) {
        return Objects.nonNull( tenantId ) && role != null;
    }

    public static User robotUser( String tenantId ) {
        requireNonNull( tenantId, "TenantId must not be blank." );

        return new User( null, null, tenantId, ROBOT );
    }

    private boolean internalIsHumanUser( ) {
        return role == TENANT_ADMIN || role == TENANT_MEMBER;
    }

    public void checkIsTenantAdmin( ) {
        internalCheckLoggedIn( );
        internalCheckTenantAdmin( );
    }

    private void internalCheckLoggedIn( ) {
        if ( !internalIsLoggedIn( ) ) {
            throw authenticationException( );
        }
    }

    private void internalCheckTenantAdmin( ) {
        if ( !internalIsTenantAdmin( ) ) {
            throw accessDeniedException( );
        }
    }

    private boolean internalIsTenantAdmin( ) {
        return role == TENANT_ADMIN;
    }

    public boolean isTenantAdmin( ) {
        if ( !internalIsLoggedIn( ) ) {
            return false;
        }

        return internalIsTenantAdmin( );
    }

    public void checkIsLoggedInFor( String tenantId ) {
        requireNonBlank( tenantId, "TenantId must not be blank." );

        internalCheckLoggedIn( );
        internalCheckTenantFor( tenantId );
    }

    private boolean isWrongTenantFor( String tenantId ) {
        return !Objects.equals( this.tenantId, tenantId );
    }


    private void internalCheckTenantFor( String tenantId ) {
        if ( isWrongTenantFor( tenantId ) ) {
            throw new MryException( WRONG_TENANT, "租户错误。", "userTenantId", this.tenantId( ), "tenantId", tenantId );
        }
    }

}
