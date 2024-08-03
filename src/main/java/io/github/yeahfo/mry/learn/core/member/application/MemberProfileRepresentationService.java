package io.github.yeahfo.mry.learn.core.member.application;

import io.github.yeahfo.mry.learn.common.ratelimit.RateLimiter;
import io.github.yeahfo.mry.learn.core.app.domain.App;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.member.application.representation.ConsoleMemberProfileRepresentation;
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

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public record MemberProfileRepresentationService( RateLimiter rateLimiter,
                                                  MongoTemplate mongoTemplate,
                                                  TenantRepository tenantRepository,
                                                  MemberRepository memberRepository ) {

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
}
