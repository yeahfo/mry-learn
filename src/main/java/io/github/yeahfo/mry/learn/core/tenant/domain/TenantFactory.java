package io.github.yeahfo.mry.learn.core.tenant.domain;

import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.departmenthierarchy.domain.DepartmentHierarchy;
import io.github.yeahfo.mry.learn.core.member.domain.Member;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TenantFactory {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder( );

    public static TenantMadeHolder make( String tenantName, String mobile, String email,
                                         String password, User user ) {
        Tenant tenant = new Tenant( tenantName, user );
        DepartmentHierarchy departmentHierarchy = new DepartmentHierarchy( user );
        Member member = new Member( mobile, email, passwordEncoder.encode( password ), user );
        return new TenantMadeHolder( tenant, member, departmentHierarchy );
    }
}
