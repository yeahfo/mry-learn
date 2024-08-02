package io.github.yeahfo.mry.learn.core.member.infrastructure;

import io.github.yeahfo.mry.learn.core.member.domain.Member;
import io.github.yeahfo.mry.learn.core.member.domain.MemberRepository;
import io.github.yeahfo.mry.learn.core.member.domain.TenantCachedMember;
import io.github.yeahfo.mry.learn.core.member.infrastructure.mongo.MemberMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

import static io.github.yeahfo.mry.learn.core.common.utils.CommonUtils.requireNonBlank;
import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final MongoTemplate mongoTemplate;
    private final MemberMongoRepository mongoRepository;


    @Override
    public Member save( Member member ) {
        return mongoRepository.save( member );
    }

    @Cacheable( value = MEMBER_CACHE, key = "#id", unless = "#result == null" )
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
        return mongoRepository.existsByMobileOrEmail( mobileOrEmail, mobileOrEmail );
    }

    @Override
    public Optional< Member > findByMobileOrEmail( String mobileOrEmail ) {
        return mongoRepository.findByMobileOrEmail( mobileOrEmail, mobileOrEmail );
    }

    @Override
    public Optional< Member > findByWxUnionId( String wxUnionId ) {
        requireNonBlank( wxUnionId, "WxUnionId must not be blank." );
        return mongoRepository.findByWxUnionId( wxUnionId );
    }

    @Override
    //必须返回ArrayList而非List，否则缓存中由于没有ArrayList类型信息而失败
    @Cacheable( value = TENANT_MEMBERS_CACHE, key = "#tenantId" )
    public ArrayList< TenantCachedMember > findTenantCachedMembers( String tenantId ) {
        requireNonBlank( tenantId, "Tenant ID must not be blank." );

        Query query = query( where( "tenantId" ).is( tenantId ) );
        query.fields( ).include( "name", "role", "mobile", "email", "mobileWxOpenId", "customId", "departmentIds", "active" );
        return new ArrayList<>( mongoTemplate.find( query, TenantCachedMember.class, MEMBER_COLLECTION ) );
    }

    @Override
    public void delete( Member member ) {
        this.mongoRepository.delete( member );
        this.evictMemberCache( member.identifier( ) );
        this.evictTenantMembersCache( member.tenantId( ) );
    }


    @Caching( evict = { @CacheEvict( value = MEMBER_CACHE, key = "#memberId" ) } )
    public void evictMemberCache( String memberId ) {
        requireNonBlank( memberId, "Member ID must not be blank." );

        log.info( "Evicted cache for member[{}].", memberId );
    }

    @Caching( evict = { @CacheEvict( value = TENANT_MEMBERS_CACHE, key = "#tenantId" ) } )
    public void evictTenantMembersCache( String tenantId ) {
        requireNonBlank( tenantId, "Tenant ID must not be blank." );

        log.info( "Evicted all members cache for tenant[{}].", tenantId );
    }
}
