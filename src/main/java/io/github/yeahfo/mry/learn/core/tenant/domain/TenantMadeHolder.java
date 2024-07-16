package io.github.yeahfo.mry.learn.core.tenant.domain;

import io.github.yeahfo.mry.learn.core.departmenthierarchy.domain.DepartmentHierarchy;
import io.github.yeahfo.mry.learn.core.member.domain.Member;

public record TenantMadeHolder( Tenant tenant,
                                Member member,
                                DepartmentHierarchy departmentHierarchy ) {
}
