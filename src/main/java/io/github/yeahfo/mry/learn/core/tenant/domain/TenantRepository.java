package io.github.yeahfo.mry.learn.core.tenant.domain;

import io.github.yeahfo.mry.learn.core.common.domain.AggregateRootRepository;

public interface TenantRepository extends AggregateRootRepository< Tenant, String > {
    boolean exists( String tenantId );

    PackagesStatus packagesStatusOf( String tenantId );

    boolean existsBySubdomainPrefix( String subdomainPrefix );
}
