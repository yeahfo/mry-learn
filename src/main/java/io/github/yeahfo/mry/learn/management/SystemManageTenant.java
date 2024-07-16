package io.github.yeahfo.mry.learn.management;

import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.github.yeahfo.mry.learn.core.common.domain.Role;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.departmenthierarchy.domain.DepartmentHierarchyRepository;
import io.github.yeahfo.mry.learn.core.member.domain.MemberRepository;
import io.github.yeahfo.mry.learn.core.tenant.domain.Tenant;
import io.github.yeahfo.mry.learn.core.tenant.domain.TenantFactory;
import io.github.yeahfo.mry.learn.core.tenant.domain.TenantMadeHolder;
import io.github.yeahfo.mry.learn.core.tenant.domain.TenantRepository;
import io.github.yeahfo.mry.learn.core.tenant.domain.event.TenantPlanUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static io.github.yeahfo.mry.learn.core.plan.domain.PlanType.FLAGSHIP;
import static java.time.ZoneId.systemDefault;

@Component
public class SystemManageTenant {
    public static final String MANAGE_TENANT_ID = "100000000";
    public static final String ADMIN_MEMBER_ID = MANAGE_TENANT_ID;
    public static final String ADMIN_MEMBER_NAME = "老板";
    public static final String ADMIN_INIT_MOBILE = "18888888888";
    public static final String ADMIN_INIT_PASSWORD = "95279527";
    public static final User MANAGE_ROBOT_USER = User.robotUser( MANAGE_TENANT_ID );
    private static final Logger log = LoggerFactory.getLogger( SystemManageTenant.class );

    private final TenantRepository tenantRepository;
    private final MemberRepository memberRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final DepartmentHierarchyRepository departmentHierarchyRepository;

    public SystemManageTenant( TenantRepository tenantRepository,
                               MemberRepository memberRepository,
                               DomainEventPublisher domainEventPublisher,
                               DepartmentHierarchyRepository departmentHierarchyRepository ) {
        this.tenantRepository = tenantRepository;
        this.memberRepository = memberRepository;
        this.domainEventPublisher = domainEventPublisher;
        this.departmentHierarchyRepository = departmentHierarchyRepository;
    }

    @Transactional
    public void init( ) {
        if ( tenantRepository.exists( MANAGE_TENANT_ID ) ) {
            return;
        }

        User user = User.humanUser( ADMIN_MEMBER_ID, ADMIN_MEMBER_NAME, MANAGE_TENANT_ID, Role.TENANT_ADMIN );
        TenantMadeHolder madeHolder = TenantFactory.make( "自营", ADMIN_INIT_MOBILE, null, ADMIN_INIT_PASSWORD, user );
        memberRepository.save( madeHolder.member( ) );
        Tenant tenant = madeHolder.tenant( );
        tenant.updatePlanType( FLAGSHIP, LocalDate.of( 2099, 12, 31 ).atStartOfDay( systemDefault( ) ).toInstant( ), user );
        tenantRepository.save( tenant );
        departmentHierarchyRepository.save( madeHolder.departmentHierarchy( ) );
        domainEventPublisher.publish( Tenant.class, tenant, List.of( new TenantPlanUpdatedEvent( FLAGSHIP, user ) ) );
        log.info( "Created system manage tenant." );
    }
}
