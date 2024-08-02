package io.github.yeahfo.mry.learn.core.member.domain;

import io.github.yeahfo.mry.learn.core.common.domain.AggregateRootRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface MemberRepository extends AggregateRootRepository< Member, String > {
    boolean existsByMobile( String mobile );

    boolean existsByEmail( String email );

    boolean existsByCustomId( String customId, String tenantId );

    boolean existsByMobileOrEmail( String mobileOrEmail );

    Optional< Member > findByMobileOrEmail( String mobileOrEmail );

    Optional< Member > findByWxUnionId( String wxUnionId );

    ArrayList< TenantCachedMember > findTenantCachedMembers( String tenantId );

    void delete( Member member );
}
