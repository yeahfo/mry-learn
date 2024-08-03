package io.github.yeahfo.mry.learn.core.departmenthierarchy.domain;

import io.github.yeahfo.mry.learn.core.common.domain.AggregateRootRepository;

public interface DepartmentHierarchyRepository extends AggregateRootRepository< DepartmentHierarchy, String > {
    DepartmentHierarchy findByTenantId( String tenantId );
}
