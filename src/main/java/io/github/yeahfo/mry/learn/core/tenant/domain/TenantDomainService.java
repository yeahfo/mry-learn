package io.github.yeahfo.mry.learn.core.tenant.domain;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.github.yeahfo.mry.learn.core.tenant.domain.event.TenantDomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.TENANT_WITH_SUBDOMAIN_PREFIX_ALREADY_EXISTS;
import static io.github.yeahfo.mry.learn.core.common.utils.MapUtils.mapOf;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@RequiredArgsConstructor
public class TenantDomainService {
    private final TenantRepository tenantRepository;

    public ResultWithDomainEvents< Tenant, TenantDomainEvent > updateSubdomain( Tenant tenant, String subdomainPrefix, User user ) {
        if ( isNotBlank( subdomainPrefix )
             && !Objects.equals( tenant.subdomainPrefix( ), subdomainPrefix )
             && tenantRepository.existsBySubdomainPrefix( subdomainPrefix ) ) {
            throw new MryException( TENANT_WITH_SUBDOMAIN_PREFIX_ALREADY_EXISTS,
                    "更新失败，域名已经被占用。", mapOf( "subdomainPrefix", subdomainPrefix ) );
        }
        return tenant.updateSubdomain( subdomainPrefix, user );
    }
}
