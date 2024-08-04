package io.github.yeahfo.mry.learn.core.member.application;

import io.github.yeahfo.mry.learn.common.ratelimit.RateLimiter;
import io.github.yeahfo.mry.learn.core.app.domain.App;
import io.github.yeahfo.mry.learn.core.common.application.PagedRepresentation;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.utils.Pagination;
import io.github.yeahfo.mry.learn.core.department.domain.DepartmentRepository;
import io.github.yeahfo.mry.learn.core.department.domain.TenantCachedDepartment;
import io.github.yeahfo.mry.learn.core.departmenthierarchy.domain.DepartmentHierarchy;
import io.github.yeahfo.mry.learn.core.departmenthierarchy.domain.DepartmentHierarchyRepository;
import io.github.yeahfo.mry.learn.core.member.application.command.ListMyManagedMembersCommand;
import io.github.yeahfo.mry.learn.core.member.application.representation.*;
import io.github.yeahfo.mry.learn.core.member.domain.Member;
import io.github.yeahfo.mry.learn.core.member.domain.MemberReference;
import io.github.yeahfo.mry.learn.core.member.domain.MemberRepository;
import io.github.yeahfo.mry.learn.core.tenant.application.representation.ConsoleTenantProfileRepresentation;
import io.github.yeahfo.mry.learn.core.tenant.application.representation.PackagesStatusRepresentation;
import io.github.yeahfo.mry.learn.core.tenant.domain.Packages;
import io.github.yeahfo.mry.learn.core.tenant.domain.PackagesStatus;
import io.github.yeahfo.mry.learn.core.tenant.domain.Tenant;
import io.github.yeahfo.mry.learn.core.tenant.domain.TenantRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static io.github.yeahfo.mry.learn.core.common.application.PagedRepresentation.pagedList;
import static io.github.yeahfo.mry.learn.core.common.utils.CommonUtils.*;
import static io.github.yeahfo.mry.learn.core.common.utils.MongoCriteriaUtils.regexSearch;
import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.CHINESE_COLLATOR;
import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.MEMBER_COLLECTION;
import static io.github.yeahfo.mry.learn.core.common.utils.Pagination.pagination;
import static io.github.yeahfo.mry.learn.core.common.validation.id.member.MemberIdValidator.isMemberId;
import static java.util.regex.Pattern.matches;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.by;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public record MemberRepresentationService( RateLimiter rateLimiter,
                                           MongoTemplate mongoTemplate,
                                           TenantRepository tenantRepository,
                                           MemberRepository memberRepository,
                                           DepartmentRepository departmentRepository,
                                           DepartmentHierarchyRepository departmentHierarchyRepository ) {
    private final static Set< String > ALLOWED_SORT_FIELDS = Set.of( "name", "active" );

    public ConsoleMemberProfileRepresentation fetchMyProfile( User user ) {
        rateLimiter.applyFor( user.tenantId( ), "Member:FetchMyProfile", 100 );

        String memberId = user.memberId( );
        String tenantId = user.tenantId( );

        Member member = memberRepository.findByIdExacted( memberId );
        Tenant tenant = tenantRepository.findByIdExacted( tenantId );

        boolean hasManagedApps;
        if ( user.isTenantAdmin( ) ) {
            hasManagedApps = true;
        } else {
            Query query = query( where( "tenantId" ).is( user.tenantId( ) ).and( "managers" ).is( memberId ) );
            hasManagedApps = mongoTemplate.exists( query, App.class );
        }

        Packages packages = tenant.packages( );
        PackagesStatus packagesStatus = tenant.packagesStatus( );

        PackagesStatusRepresentation planProfile = PackagesStatusRepresentation.builder( )
                .planName( packages.currentPlan( ).name( ) )
                .planType( packages.currentPlanType( ) )
                .effectivePlanName( packages.effectivePlan( ).name( ) )
                .effectivePlanType( packages.effectivePlanType( ) )
                .maxAppReached( packagesStatus.isMaxAppReached( ) )
                .maxMemberReached( packagesStatus.isMaxMemberReached( ) )
                .submissionNotifyAllowed( packages.submissionNotifyAllowed( ) )
                .batchImportQrAllowed( packages.batchImportQrAllowed( ) )
                .batchImportMemberAllowed( packages.batchImportMemberAllowed( ) )
                .submissionApprovalAllowed( packages.submissionApprovalAllowed( ) )
                .reportingAllowed( packages.reportingAllowed( ) )
                .customSubdomainAllowed( packages.customSubdomainAllowed( ) )
                .developerAllowed( packages.developerAllowed( ) )
                .customLogoAllowed( packages.customLogoAllowed( ) )
                .videoAudioAllowed( packages.videoAudioAllowed( ) )
                .assignmentAllowed( packages.assignmentAllowed( ) )
                .supportedControlTypes( packages.effectiveSupportedControlTypes( ) )
                .expired( packages.isExpired( ) )
                .expireAt( packages.expireAt( ) )
                .build( );

        ConsoleTenantProfileRepresentation tenantProfile = ConsoleTenantProfileRepresentation.builder( )
                .tenantId( tenantId )
                .name( tenant.name( ) )
                .subdomainPrefix( tenant.subdomainPrefix( ) )
                .baseDomainName( "commonProperties.getBaseDomainName( )" )
                .subdomainReady( tenant.subdomainReady( ) )
                .logo( tenant.logo( ) )
                .packagesStatus( planProfile )
                .build( );

        return ConsoleMemberProfileRepresentation.builder( )
                .memberId( memberId )
                .tenantId( member.tenantId( ) )
                .name( member.name( ) )
                .role( member.role( ) )
                .avatar( member.avatar( ) )
                .hasManagedApps( hasManagedApps )
                .tenantProfile( tenantProfile )
                .topAppIds( member.toppedAppIds( ) )
                .mobileIdentified( member.mobileIdentified( ) )
                .build( );
    }

    public ClientMemberProfileRepresentation fetchMyClientMemberProfile( User user ) {
        rateLimiter.applyFor( user.tenantId( ), "Member:FetchMyClientProfile", 100 );

        String memberId = user.memberId( );
        String tenantId = user.tenantId( );

        Member member = memberRepository.findByIdExacted( memberId );
        Tenant tenant = tenantRepository.findByIdExacted( tenantId );

        return ClientMemberProfileRepresentation.builder( )
                .memberId( memberId )
                .memberName( member.name( ) )
                .avatar( member.avatar( ) )
                .tenantId( tenantId )
                .tenantName( tenant.name( ) )
                .tenantLogo( tenant.logo( ) )
                .subdomainPrefix( tenant.subdomainPrefix( ) )
                .subdomainReady( tenant.subdomainReady( ) )
                .topAppIds( member.toppedAppIds( ) )
                .hideBottomMryLogo( tenant.packages( ).hideBottomMryLogo( ) )
                .reportingAllowed( tenant.packages( ).reportingAllowed( ) )
                .kanbanAllowed( tenant.packages( ).kanbanAllowed( ) )
                .assignmentAllowed( tenant.packages( ).assignmentAllowed( ) )
                .build( );
    }

    public MemberInfoRepresentation fetchMyMemberInfo( User user ) {
        rateLimiter.applyFor( user.tenantId( ), "Member:FetchMyMemberInfo", 10 );

        String memberId = user.memberId( );
        Member member = memberRepository.findByIdExacted( memberId );
        List< String > departmentIds = member.departmentIds( );
        List< String > departmentNames = List.of( );

        if ( isNotEmpty( departmentIds ) ) {
            DepartmentHierarchy departmentHierarchy = departmentHierarchyRepository.findByTenantId( user.tenantId( ) );
            List< TenantCachedDepartment > cachedDepartments = departmentRepository.findAllDepartmentsByTenantId( departmentHierarchy.tenantId( ) );
            Map< String, String > allDepartmentNames = cachedDepartments.stream( )
                    .collect( toImmutableMap( TenantCachedDepartment::id, TenantCachedDepartment::name ) );
            Map< String, String > departmentFullNames = departmentHierarchy.departmentFullNames( allDepartmentNames );
            departmentNames = departmentIds.stream( ).map( departmentFullNames::get ).filter( Objects::nonNull ).collect( toImmutableList( ) );
        }

        return MemberInfoRepresentation.builder( )
                .memberId( member.identifier( ) )
                .tenantId( member.tenantId( ) )
                .name( member.name( ) )
                .email( member.email( ) )
                .mobile( member.mobile( ) )
                .wxNickName( member.wxNickName( ) )
                .wxBound( member.isWxBound( ) )
                .role( member.role( ) )
                .departments( departmentNames )
                .build( );
    }

    public MemberBaseSettingRepresentation fetchMyBaseSetting( User user ) {
        rateLimiter.applyFor( user.tenantId( ), "Member:FetchMyBaseSetting", 10 );

        String memberId = user.memberId( );
        Member member = memberRepository.findByIdExacted( memberId );
        return MemberBaseSettingRepresentation.builder( ).id( memberId ).name( member.name( ) ).build( );
    }

    public PagedRepresentation< ListMemberRepresentation > listMyManagedMembers( ListMyManagedMembersCommand command, User user ) {
        String tenantId = user.tenantId( );
        rateLimiter.applyFor( tenantId, "Member:List", 10 );

        Pagination pagination = pagination( command.page( ), command.size( ) );
        String departmentId = command.departmentId( );
        String search = command.search( );

        Query query = new Query( buildMemberQueryCriteria( tenantId, departmentId, search ) );

        long count = mongoTemplate.count( query, Member.class );
        if ( count == 0 ) {
            return pagedList( pagination, 0, List.of( ) );
        }

        query.skip( pagination.skip( ) ).limit( pagination.limit( ) ).with( sort( command ) );
        query.fields( ).include( "name" ).include( "avatar" ).include( "role" ).include( "mobile" )
                .include( "wxUnionId" ).include( "wxNickName" ).include( "email" )
                .include( "active" ).include( "createdAt" ).include( "departmentIds" );
        List< ListMemberRepresentation > members = mongoTemplate.find( query, ListMemberRepresentation.class, MEMBER_COLLECTION );
        return pagedList( pagination, ( int ) count, members );
    }

    public List< MemberReferenceRepresentation > listMemberReferences( User user ) {
        rateLimiter.applyFor( user.tenantId( ), "Member:FetchAllMemberReferences", 100 );

        return doListMemberReferences( user.tenantId( ) );
    }

    public List< MemberReferenceRepresentation > listMemberReferencesForTenant( String tenantId, User user ) {
        rateLimiter.applyFor(user.tenantId(), "Member:FetchAllMemberReferencesTenant", 100);

        user.checkIsLoggedInFor(tenantId);
        return doListMemberReferences(user.tenantId());
    }

    private List< MemberReferenceRepresentation > doListMemberReferences( String tenantId ) {
        List< MemberReference > memberReferences = memberRepository.findAllMemberReferences( tenantId );
        return memberReferences.stream( )
                .sorted( ( o1, o2 ) -> CHINESE_COLLATOR.compare( o1.name( ), o2.name( ) ) )
                .map( member -> {
                    String suffix = isNotBlank( member.mobile( ) ) ? "（" + maskMobile( member.mobile( ) ) + "）" : "";

                    return MemberReferenceRepresentation.builder( )
                            .id( member.id( ) )
                            .showName( member.name( ) + suffix )
                            .build( );
                } )
                .collect( toImmutableList( ) );
    }

    private Criteria buildMemberQueryCriteria( String tenantId,
                                               String departmentId,
                                               String search ) {
        Criteria criteria = where( "tenantId" ).is( tenantId );

        if ( isNotBlank( departmentId ) ) {
            criteria.and( "departmentIds" ).is( departmentId );
        }

        //1. search为空时返回
        if ( isBlank( search ) ) {
            return criteria;
        }

        //2. 直接根据id搜索
        if ( isMemberId( search ) ) {
            return criteria.and( "_id" ).is( search );
        }

        //3. search为手机号时，精确手机号查询
        if ( isMobileNumber( search ) ) {
            return criteria.and( "mobile" ).is( search );
        }

        //4. search为邮箱时，精确邮箱查询
        if ( isEmail( search ) ) {
            return criteria.and( "email" ).is( search );
        }

        //5. 当为部分手机号时，用正则搜索MOBILE
        if ( matches( "^[0-9]{4,11}$", search ) ) {
            return criteria.and( "mobile" ).regex( search );
        }

        //6. 其他情况下，用正则搜索name或email或customId
        return criteria.orOperator( where( "customId" ).is( search ), regexSearch( "name", search ), where( "email" ).regex( search ) );
    }


    private Sort sort( ListMyManagedMembersCommand command ) {
        String sortedBy = command.sortedBy( );

        if ( isBlank( sortedBy ) || !ALLOWED_SORT_FIELDS.contains( sortedBy ) ) {
            return by( DESC, "createdAt" );
        }

        Sort.Direction direction = command.ascSort( ) ? ASC : DESC;
        if ( Objects.equals( sortedBy, "createdAt" ) ) {
            return by( direction, "createdAt" );
        }

        return by( direction, sortedBy ).and( by( DESC, "createdAt" ) );
    }
}
