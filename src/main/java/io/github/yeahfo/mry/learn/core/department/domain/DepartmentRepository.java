package io.github.yeahfo.mry.learn.core.department.domain;

import io.github.yeahfo.mry.learn.core.common.domain.AggregateRootRepository;

import java.util.List;

public interface DepartmentRepository extends AggregateRootRepository< Department, Long > {
    boolean notAllDepartmentsExist( List< Long > departmentIds, Long tenantId );

    List< Department > tenantAllDepartments( Long tenantId );
}
