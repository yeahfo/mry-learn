package io.github.yeahfo.mry.learn.core.department.domain;

import io.github.yeahfo.mry.learn.core.common.domain.AggregateRootRepository;

import java.util.List;

public interface DepartmentRepository extends AggregateRootRepository< Department, String > {
    boolean notAllDepartmentsExist( List< String > departmentIds, String tenantId );

    List< Department > tenantAllDepartments( String tenantId );

    List< TenantCachedDepartment > findAllDepartmentsByTenantId( String tenantId );
}
