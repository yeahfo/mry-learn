package io.github.yeahfo.mry.learn.core.member.application;

import io.github.yeahfo.mry.learn.common.ratelimit.RateLimiter;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.member.application.representation.ConsoleMemberProfileRepresentation;
import io.github.yeahfo.mry.learn.core.member.domain.Member;
import io.github.yeahfo.mry.learn.core.member.domain.MemberRepository;
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
                                                  MemberRepository memberRepository,
                                                  ) {

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

        Packages packages = tenant.getPackages( );
        PackagesStatus packagesStatus = tenant.packagesStatus( );

        QPackagesStatus planProfile = QPackagesStatus.builder( )
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

        QConsoleTenantProfile tenantProfile = QConsoleTenantProfile.builder( )
                .tenantId( tenantId )
                .name( tenant.getName( ) )
                .subdomainPrefix( tenant.getSubdomainPrefix( ) )
                .baseDomainName( commonProperties.getBaseDomainName( ) )
                .subdomainReady( tenant.isSubdomainReady( ) )
                .logo( tenant.getLogo( ) )
                .packagesStatus( planProfile )
                .build( );

        return QConsoleMemberProfile.builder( )
                .memberId( memberId )
                .tenantId( member.getTenantId( ) )
                .name( member.getName( ) )
                .role( member.getRole( ) )
                .avatar( member.getAvatar( ) )
                .hasManagedApps( hasManagedApps )
                .tenantProfile( tenantProfile )
                .topAppIds( member.toppedAppIds( ) )
                .mobileIdentified( member.isMobileIdentified( ) )
                .build( );
    }
}
