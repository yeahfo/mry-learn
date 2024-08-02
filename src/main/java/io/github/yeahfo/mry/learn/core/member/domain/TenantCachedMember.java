package io.github.yeahfo.mry.learn.core.member.domain;

import io.github.yeahfo.mry.learn.core.common.domain.Role;
import lombok.Builder;

import java.util.List;

import static io.github.yeahfo.mry.learn.core.common.domain.Role.TENANT_ADMIN;

@Builder
public record TenantCachedMember( String id,
                                  String name,
                                  Role role,
                                  String mobile,
                                  String email,
                                  String mobileWxOpenId,
                                  String customId,
                                  List< String > departmentIds,
                                  boolean active ) {
    public boolean isTenantAdmin( ) {
        return this.role == TENANT_ADMIN;
    }
}
