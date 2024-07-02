package io.github.yeahfo.mry.learn.common.domain;

import static io.github.yeahfo.mry.learn.common.domain.Role.ROBOT;
import static io.github.yeahfo.mry.learn.common.utils.CommonUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

public record User( String memberId,
                    String name,
                    String tenantId,
                    Role role ) {
    public static final User NOUSER = new User( null, null, null, null );
    public static final User ANONYMOUS_USER = NOUSER;

    public static User humanUser( String memberId, String name, String tenantId, Role role ) {
        requireNonBlank( memberId, "MemberId must not be blank." );
        requireNonBlank( name, "Name must not be blank." );
        requireNonBlank( tenantId, "TenantId must not be blank." );
        requireNonNull( role, "Role must not be null." );

        if ( role == ROBOT ) {
            throw new IllegalStateException( "Human user should not have ROBOT role." );
        }

        return new User( memberId, name, tenantId, role );
    }

    public static User robotUser( String tenantId ) {
        requireNonBlank( tenantId, "TenantId must not be blank." );
        return new User( null, null, tenantId, ROBOT );
    }
}
