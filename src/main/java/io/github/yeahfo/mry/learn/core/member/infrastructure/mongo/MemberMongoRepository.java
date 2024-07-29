package io.github.yeahfo.mry.learn.core.member.infrastructure.mongo;

import io.github.yeahfo.mry.learn.core.member.domain.Member;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MemberMongoRepository extends MongoRepository< Member, String > {
    boolean existsByMobile( String mobile );

    boolean existsByEmail( String email );

    boolean existsByCustomId( String customId );

    boolean existsByMobileOrEmail( String mobile, String email );

    Optional< Member > findByMobileOrEmail( String mobile, String email );

    Optional< Member > findByWxUnionId( String wxUnionId );
}
