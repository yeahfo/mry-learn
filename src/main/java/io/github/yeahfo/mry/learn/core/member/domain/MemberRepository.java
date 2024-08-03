package io.github.yeahfo.mry.learn.core.member.domain;

import io.github.yeahfo.mry.learn.core.common.domain.AggregateRootRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static io.github.yeahfo.mry.learn.core.common.utils.CommonUtils.requireNonBlank;

public interface MemberRepository extends AggregateRootRepository< Member, String > {
    boolean existsByMobile( String mobile );

    boolean existsByEmail( String email );

    boolean existsByCustomId( String customId, String tenantId );

    boolean existsByMobileOrEmail( String mobileOrEmail );

    Optional< Member > findByMobileOrEmail( String mobileOrEmail );

    Optional< Member > findByWxUnionId( String wxUnionId );

    ArrayList< TenantCachedMember > findTenantCachedMembers( String tenantId );

    void delete( Member member );

    default List< MemberReference > findAllMemberReferences( String tenantId ){
        requireNonBlank(tenantId, "Tenant ID must not be blank.");
        return findTenantCachedMembers( tenantId )
                .stream()
                .map(toMemberReference())
                .collect(toImmutableList());
    }

    default Function< TenantCachedMember, MemberReference > toMemberReference( ) {
        return cachedMember -> MemberReference.builder( )
                .id( cachedMember.id( ) )
                .name( cachedMember.name( ) )
                .mobile( cachedMember.mobile( ) )
                .email( cachedMember.email( ) )
                .build( );
    }
}
