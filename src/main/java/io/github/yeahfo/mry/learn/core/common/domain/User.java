package io.github.yeahfo.mry.learn.core.common.domain;

import java.util.Objects;

import static io.github.yeahfo.mry.learn.core.common.domain.Role.*;
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

    public boolean isHumanUser() {
        if (!internalIsLoggedIn()) {
            return false;
        }

        return internalIsHumanUser();
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
    private boolean internalIsHumanUser() {
        return role == TENANT_ADMIN || role == TENANT_MEMBER;
    }
}
