package io.github.yeahfo.mry.learn.core.register.application;

import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.github.yeahfo.mry.learn.common.ratelimit.RateLimiter;
import io.github.yeahfo.mry.learn.core.common.domain.Role;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.departmenthierarchy.domain.DepartmentHierarchyRepository;
import io.github.yeahfo.mry.learn.core.member.domain.Member;
import io.github.yeahfo.mry.learn.core.member.domain.MemberRepository;
import io.github.yeahfo.mry.learn.core.member.domain.event.MemberCreatedEvent;
import io.github.yeahfo.mry.learn.core.register.domain.RegisterDomainService;
import io.github.yeahfo.mry.learn.core.tenant.domain.Tenant;
import io.github.yeahfo.mry.learn.core.tenant.domain.TenantMadeHolder;
import io.github.yeahfo.mry.learn.core.tenant.domain.TenantRepository;
import io.github.yeahfo.mry.learn.core.tenant.domain.event.TenantCreatedEvent;
import io.github.yeahfo.mry.learn.core.verification.domain.VerificationCodeChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static io.github.yeahfo.mry.learn.core.verification.domain.VerificationCodeType.REGISTER;
import static java.util.Collections.singletonList;

@Service
public class RegisterCommandService {
    private static final Logger log = LoggerFactory.getLogger( RegisterCommandService.class );
    private final RateLimiter rateLimiter;
    private final TenantRepository tenantRepository;
    private final MemberRepository memberRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final RegisterDomainService registerDomainService;
    private final VerificationCodeChecker verificationCodeChecker;
    private final DepartmentHierarchyRepository departmentHierarchyRepository;

    public RegisterCommandService( RateLimiter rateLimiter,
                                   TenantRepository tenantRepository,
                                   MemberRepository memberRepository,
                                   DomainEventPublisher domainEventPublisher,
                                   RegisterDomainService registerDomainService,
                                   VerificationCodeChecker verificationCodeChecker,
                                   DepartmentHierarchyRepository departmentHierarchyRepository ) {
        this.rateLimiter = rateLimiter;
        this.tenantRepository = tenantRepository;
        this.memberRepository = memberRepository;
        this.domainEventPublisher = domainEventPublisher;
        this.registerDomainService = registerDomainService;
        this.verificationCodeChecker = verificationCodeChecker;
        this.departmentHierarchyRepository = departmentHierarchyRepository;
    }

    @Transactional
    public RegisteredRepresentation register( RegisterCommand command ) {
        rateLimiter.applyFor( "Registration:Register:All", 20 );
        String mobileOrEmail = command.mobileOrEmail( );
        verificationCodeChecker.check( mobileOrEmail, command.verification( ), REGISTER );

        User user = User.humanUser( Member.newMemberId( ), command.username( ), Tenant.newTenantId( ), Role.TENANT_ADMIN );
        TenantMadeHolder madeHolder = registerDomainService.register( mobileOrEmail, command.password( ), command.tenantName( ), user );

        Tenant tenant = tenantRepository.save( madeHolder.tenant( ) );
        Member member = memberRepository.save( madeHolder.member( ) );
        departmentHierarchyRepository.save( madeHolder.departmentHierarchy( ) );
        this.domainEventPublisher.publish( Tenant.class, tenant, singletonList( new TenantCreatedEvent( user ) ) );
        this.domainEventPublisher.publish( Member.class, member, singletonList( new MemberCreatedEvent( user ) ) );
        log.info( "Registered tenant[{}] with admin member[{}].", tenant.id( ), member.id( ) );
        return new RegisteredRepresentation( tenant.identifier( ), member.identifier( ) );
    }
}
