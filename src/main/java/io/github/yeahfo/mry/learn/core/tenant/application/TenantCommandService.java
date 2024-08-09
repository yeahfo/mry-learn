package io.github.yeahfo.mry.learn.core.tenant.application;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.github.yeahfo.mry.learn.common.ratelimit.RateLimiter;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.tenant.application.command.UpdateTenantBaseSettingCommand;
import io.github.yeahfo.mry.learn.core.tenant.application.command.UpdateTenantLogoCommand;
import io.github.yeahfo.mry.learn.core.tenant.application.command.UpdateTenantSubdomainCommand;
import io.github.yeahfo.mry.learn.core.tenant.domain.Tenant;
import io.github.yeahfo.mry.learn.core.tenant.domain.TenantDomainService;
import io.github.yeahfo.mry.learn.core.tenant.domain.TenantRepository;
import io.github.yeahfo.mry.learn.core.tenant.domain.event.TenantDomainEvent;
import io.github.yeahfo.mry.learn.core.tenant.domain.event.TenantDomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantCommandService {
    private final RateLimiter rateLimiter;
    private final TenantRepository tenantRepository;
    private final TenantDomainService tenantDomainService;
    private final TenantDomainEventPublisher domainEventPublisher;

    @Transactional
    public void updateTenantBaseSetting( UpdateTenantBaseSettingCommand command, User user ) {
        user.checkIsTenantAdmin( );
        rateLimiter.applyFor( user.tenantId( ), "Tenant:UpdateBaseSetting", 5 );

        Tenant tenant = tenantRepository.findByIdExacted( user.tenantId( ) );
        ResultWithDomainEvents< Tenant, TenantDomainEvent > resultWithDomainEvents = tenant
                .updateBaseSetting( command.name( ), command.loginBackground( ), user );
        Tenant saved = tenantRepository.save( tenant );
        log.info( "Updated base setting for tenant[{}].", user.tenantId( ) );
        domainEventPublisher.publish( saved, resultWithDomainEvents.events );
    }

    @Transactional
    public void updateTenantLogo( UpdateTenantLogoCommand command, User user ) {
        user.checkIsTenantAdmin( );
        rateLimiter.applyFor( user.tenantId( ), "Tenant:UpdateLogo", 5 );

        Tenant tenant = tenantRepository.findByIdExacted( user.tenantId( ) );
        tenant.packagesStatus( ).validateUpdateLogo( );

        tenant.updateLogo( command.logo( ), user );
        tenantRepository.save( tenant );
        log.info( "Updated logo for tenant[{}].", user.tenantId( ) );
    }

    @Transactional
    public void updateTenantSubdomain( UpdateTenantSubdomainCommand command, User user ) {
        user.checkIsTenantAdmin( );
        rateLimiter.applyFor( user.tenantId( ), "Tenant:UpdateSubdomain", 5 );

        Tenant tenant = tenantRepository.findByIdExacted( user.tenantId( ) );
        tenant.packagesStatus( ).validateUpdateSubdomain( );

        ResultWithDomainEvents< Tenant, TenantDomainEvent > resultWithDomainEvents = tenantDomainService.updateSubdomain(
                tenant, command.subdomainPrefix( ), user );
        tenantRepository.save( tenant );
        log.info( "Updated subdomain for tenant[{}] with prefix[{}].", user.tenantId( ), command.subdomainPrefix( ) );
        domainEventPublisher.publish( tenant, resultWithDomainEvents.events );
    }
}
