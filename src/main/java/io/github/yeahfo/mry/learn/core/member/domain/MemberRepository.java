package io.github.yeahfo.mry.learn.core.member.domain;

import io.github.yeahfo.mry.learn.core.common.domain.AggregateRootRepository;

public interface MemberRepository extends AggregateRootRepository< Member, Long > {
    boolean existsByMobile( String mobile );

    boolean existsByEmail( String email );

    boolean existsByCustomId( String customId, Long tenantId );
}
