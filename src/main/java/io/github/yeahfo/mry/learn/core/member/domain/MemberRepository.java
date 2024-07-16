package io.github.yeahfo.mry.learn.core.member.domain;

import io.github.yeahfo.mry.learn.core.common.domain.AggregateRootRepository;

public interface MemberRepository extends AggregateRootRepository< Member, String > {
    boolean existsByMobile( String mobile );

    boolean existsByEmail( String email );

    boolean existsByCustomId( String customId, String tenantId );

    boolean existsByMobileOrEmail( String mobileOrEmail );
}
