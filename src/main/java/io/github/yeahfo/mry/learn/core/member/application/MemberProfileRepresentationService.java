package io.github.yeahfo.mry.learn.core.member.application;

import io.github.yeahfo.mry.learn.common.ratelimit.RateLimiter;
import io.github.yeahfo.mry.learn.core.app.domain.App;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.department.domain.DepartmentRepository;
import io.github.yeahfo.mry.learn.core.department.domain.TenantCachedDepartment;
import io.github.yeahfo.mry.learn.core.departmenthierarchy.domain.DepartmentHierarchy;
import io.github.yeahfo.mry.learn.core.departmenthierarchy.domain.DepartmentHierarchyRepository;
import io.github.yeahfo.mry.learn.core.member.application.representation.ClientMemberProfileRepresentation;
import io.github.yeahfo.mry.learn.core.member.application.representation.ConsoleMemberProfileRepresentation;
import io.github.yeahfo.mry.learn.core.member.application.representation.MemberBaseSettingRepresentation;
import io.github.yeahfo.mry.learn.core.member.application.representation.MemberInfoRepresentation;
import io.github.yeahfo.mry.learn.core.member.domain.Member;
import io.github.yeahfo.mry.learn.core.member.domain.MemberRepository;
import io.github.yeahfo.mry.learn.core.tenant.application.representation.ConsoleTenantProfileRepresentation;
import io.github.yeahfo.mry.learn.core.tenant.application.representation.PackagesStatusRepresentation;
import io.github.yeahfo.mry.learn.core.tenant.domain.Packages;
import io.github.yeahfo.mry.learn.core.tenant.domain.PackagesStatus;
import io.github.yeahfo.mry.learn.core.tenant.domain.Tenant;
import io.github.yeahfo.mry.learn.core.tenant.domain.TenantRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public record MemberProfileRepresentationService( RateLimiter rateLimiter,
                                                  MongoTemplate mongoTemplate,
                                                  TenantRepository tenantRepository,
                                                  MemberRepository memberRepository,
                                                  DepartmentRepository departmentRepository,
                                                  DepartmentHierarchyRepository departmentHierarchyRepository ) {

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
}
