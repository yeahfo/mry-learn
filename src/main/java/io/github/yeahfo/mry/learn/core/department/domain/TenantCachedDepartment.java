package io.github.yeahfo.mry.learn.core.department.domain;

import lombok.Builder;

import java.util.List;

@Builder
public record TenantCachedDepartment( String id,
                                      String name,
                                      List< String > managers,
                                      String customId ) {
}
