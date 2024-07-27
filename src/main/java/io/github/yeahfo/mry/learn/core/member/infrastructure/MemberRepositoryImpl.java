package io.github.yeahfo.mry.learn.core.member.infrastructure;

import io.github.yeahfo.mry.learn.core.member.domain.Member;
import io.github.yeahfo.mry.learn.core.member.domain.MemberRepository;
import io.github.yeahfo.mry.learn.core.member.infrastructure.mongo.MemberMongoRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static io.github.yeahfo.mry.learn.core.common.utils.CommonUtils.requireNonBlank;
import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.MEMBER_CACHE;

@Repository
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberMongoRepository mongoRepository;

    public MemberRepositoryImpl( MemberMongoRepository mongoRepository ) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Member save( Member member ) {
        return mongoRepository.save( member );
    }

    @Cacheable( value = MEMBER_CACHE, key = "#id" )
    @Override
    public Optional< Member > findById( String id ) {
        return mongoRepository.findById( id );
    }

    @Override
    public boolean existsByMobile( String mobile ) {
        return mongoRepository.existsByMobile( mobile );
    }

    @Override
    public boolean existsByEmail( String email ) {
        return mongoRepository.existsByEmail( email );
    }

    @Override
    public boolean existsByCustomId( String customId, String tenantId ) {
        return mongoRepository.existsByCustomId( customId );
    }

    @Override
    public boolean existsByMobileOrEmail( String mobileOrEmail ) {
        requireNonBlank( mobileOrEmail, "Mobile or email must not be blank." );
        boolean b = mongoRepository.existsByMobileOrEmail( mobileOrEmail, mobileOrEmail );
        return b;
    }

    @Override
    public Optional< Member > findByMobileOrEmail( String mobileOrEmail ) {
        return mongoRepository.findByMobileOrEmail( mobileOrEmail, mobileOrEmail );
    }
}
