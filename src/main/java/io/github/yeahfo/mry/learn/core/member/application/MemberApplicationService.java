package io.github.yeahfo.mry.learn.core.member.application;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.github.yeahfo.mry.learn.common.password.PasswordEncoderFactories;
import io.github.yeahfo.mry.learn.common.ratelimit.RateLimiter;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.github.yeahfo.mry.learn.core.member.application.command.*;
import io.github.yeahfo.mry.learn.core.member.application.representation.MemberImportRepresentation;
import io.github.yeahfo.mry.learn.core.member.domain.*;
import io.github.yeahfo.mry.learn.core.member.domain.event.MemberDomainEvent;
import io.github.yeahfo.mry.learn.core.member.domain.event.MemberDomainEventPublisher;
import io.github.yeahfo.mry.learn.core.tenant.domain.PackagesStatus;
import io.github.yeahfo.mry.learn.core.tenant.domain.TenantRepository;
import io.github.yeahfo.mry.learn.core.verification.domain.VerificationCodeChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;

import static io.github.yeahfo.mry.learn.core.common.domain.Role.TENANT_ADMIN;
import static io.github.yeahfo.mry.learn.core.common.domain.Role.TENANT_MEMBER;
import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.MEMBER_NOT_FOUND_FOR_FINDBACK_PASSWORD;
import static io.github.yeahfo.mry.learn.core.common.utils.MapUtils.mapOf;
import static io.github.yeahfo.mry.learn.core.verification.domain.VerificationCodeType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberApplicationService {
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder( );
    private final RateLimiter rateLimiter;
    private final MemberFactory memberFactory;
    private final MemberImporter memberImporter;
    private final TenantRepository tenantRepository;
    private final MemberRepository memberRepository;
    private final MemberDomainService memberDomainService;
    private final MemberDomainEventPublisher domainEventPublisher;
    private final VerificationCodeChecker verificationCodeChecker;

    @Transactional
    public String createMember( CreateMemberCommand command, User user ) {
        user.checkIsTenantAdmin( );
        String tenantId = user.tenantId( );
        rateLimiter.applyFor( tenantId, "Member:Create", 5 );
        PackagesStatus packagesStatus = tenantRepository.packagesStatusOf( tenantId );
        packagesStatus.validateAddMember( );

        ResultWithDomainEvents< Member, MemberDomainEvent > resultWithDomainEvents = memberFactory.create( command.name( ),
                command.departmentIds( ), command.mobile( ), command.email( ), passwordEncoder.encode( command.password( ) ), user );
        Member member = memberRepository.save( resultWithDomainEvents.result );
        log.info( "Created member[{}].", member.identifier( ) );
        domainEventPublisher.publish( member, resultWithDomainEvents.events );
        return member.identifier( );
    }

    public MemberImportRepresentation importMembers( InputStream inputStream, User user ) {
        user.checkIsTenantAdmin( );
        rateLimiter.applyFor( user.tenantId( ), "Member:Import", 1 );
        PackagesStatus packagesStatus = tenantRepository.packagesStatusOf( user.tenantId( ) );
        packagesStatus.validateImportMember( );
        int remainingCount = packagesStatus.validateImportMembers( );

        MemberImportRepresentation representation = memberImporter.importMembers( inputStream, remainingCount, user );
        log.info( "Imported {} members for tenant[{}].", representation.importedCount( ), user.tenantId( ) );
        return representation;
    }

    @Transactional
    public void updateMember( String memberId, UpdateMemberInfoCommand command, User user ) {
        user.checkIsTenantAdmin( );
        rateLimiter.applyFor( user.tenantId( ), "Member:Update", 5 );
        Member member = memberRepository.findByIdAndCheckTenantShip( memberId, user );
        ResultWithDomainEvents< Member, MemberDomainEvent > resultWithDomainEvents = memberDomainService.updateMember( member,
                command.name( ),
                command.departmentIds( ),
                command.mobile( ),
                command.email( ),
                user );
        Member saved = this.memberRepository.save( resultWithDomainEvents.result );
        log.info( "Updated detail for member[{}].", memberId );
        this.domainEventPublisher.publish( saved, resultWithDomainEvents.events );
    }

    @Transactional
    public void updateMemberRole( String memberId, UpdateMemberRoleCommand command, User user ) {
        user.checkIsTenantAdmin( );
        rateLimiter.applyFor( user.tenantId( ), "Member:UpdateRole", 5 );

        Member member = memberRepository.findByIdAndCheckTenantShip( memberId, user );
        if ( command.role( ) == member.role( ) ) {
            return;
        }

        ResultWithDomainEvents< Member, MemberDomainEvent > resultWithDomainEvents = member.updateRole( command.role( ), user );
        Member saved = memberRepository.save( resultWithDomainEvents.result );
        this.domainEventPublisher.publish( saved, resultWithDomainEvents.events );

        if ( command.role( ) == TENANT_MEMBER ) {
            memberDomainService.checkMinTenantAdminLimit( user.tenantId( ) );
        }

        if ( command.role( ) == TENANT_ADMIN ) {
            memberDomainService.checkMaxTenantAdminLimit( user.tenantId( ) );
        }

        log.info( "Updated member[{}] role to {}.", memberId, command.role( ) );
    }

    @Transactional
    public void deleteMember( String memberId, User user ) {
        user.checkIsTenantAdmin( );
        rateLimiter.applyFor( user.tenantId( ), "Member:Delete", 5 );
        Member member = memberRepository.findByIdAndCheckTenantShip( memberId, user );
        ResultWithDomainEvents< Member, MemberDomainEvent > resultWithDomainEvents = member.onDelete( user );
        memberRepository.delete( member );
        this.domainEventPublisher.publish( member, resultWithDomainEvents.events );
        log.info( "Deleted member[{}].", memberId );
    }

    @Transactional
    public void activateMember( String memberId, User user ) {
        user.checkIsTenantAdmin( );
        rateLimiter.applyFor( user.tenantId( ), "Member:Activate", 5 );
        Member member = memberRepository.findByIdAndCheckTenantShip( memberId, user );
        ResultWithDomainEvents< Member, MemberDomainEvent > activated = member.activate( user );
        Member saved = memberRepository.save( activated.result );
        this.domainEventPublisher.publish( saved, activated.events );
        log.info( "Activated member[{}].", memberId );
    }

    @Transactional
    public void deactivateMember( String memberId, User user ) {
        user.checkIsTenantAdmin( );
        rateLimiter.applyFor( user.tenantId( ), "Member:Deactivate", 5 );
        Member member = memberRepository.findByIdAndCheckTenantShip( memberId, user );
        ResultWithDomainEvents< Member, MemberDomainEvent > deactivated = member.deactivate( user );
        memberRepository.save( member );
        this.domainEventPublisher.publish( member, deactivated.events );
        memberDomainService.checkMinTenantAdminLimit( member.tenantId( ) );
        log.info( "Deactivated member[{}].", memberId );
    }

    @Transactional
    public void resetPasswordForMember( String memberId, ResetMemberPasswordCommand command, User user ) {
        user.checkIsTenantAdmin( );
        rateLimiter.applyFor( user.tenantId( ), "Member:ResetPassword", 5 );
        Member member = memberRepository.findByIdAndCheckTenantShip( memberId, user );
        ResultWithDomainEvents< Member, MemberDomainEvent > resultWithDomainEvents = member.changePassword( command.password( ), user );
        memberRepository.save( member );
        this.domainEventPublisher.publish( member, resultWithDomainEvents.events );
        log.info( "Reset password for member[{}].", memberId );
    }

    @Transactional
    public void unbindMemberWx( String memberId, User user ) {
        user.checkIsTenantAdmin( );
        rateLimiter.applyFor( user.tenantId( ), "Member:UnbindWx", 5 );

        Member member = memberRepository.findByIdAndCheckTenantShip( memberId, user );
        member.unbindWx( user );
        memberRepository.save( member );
        log.info( "Unbound wx for member[{}].", memberId );
    }

    @Transactional
    public void changeMyPassword( ChangeMyPasswordCommand command, User user ) {
        rateLimiter.applyFor( user.tenantId( ), "Member:ChangeMyPassword", 5 );

        Member member = memberRepository.findByIdExacted( user.memberId( ) );
        memberDomainService.changeMyPassword( member, command.oldPassword( ), command.newPassword( ) );
        memberRepository.save( member );
        log.info( "Password changed by member[{}].", member.identifier( ) );
    }

    @Transactional
    public void changeMyMobile( ChangeMyMobileCommand command, User user ) {
        rateLimiter.applyFor( user.tenantId( ), "Member:ChangeMyMobile", 5 );

        String mobile = command.mobile( );
        verificationCodeChecker.check( mobile, command.verification( ), CHANGE_MOBILE );

        Member member = memberRepository.findByIdExacted( user.memberId( ) );
        memberDomainService.changeMyMobile( member, mobile, command.password( ) );
        memberRepository.save( member );
        log.info( "Mobile changed by member[{}].", member.identifier( ) );
    }

    @Transactional
    public void identifyMyMobile( IdentifyMyMobileCommand command, User user ) {
        rateLimiter.applyFor( user.tenantId( ), "Member:IdentifyMobile", 5 );

        String mobile = command.mobile( );
        verificationCodeChecker.check( mobile, command.verification( ), IDENTIFY_MOBILE );

        Member member = memberRepository.findByIdExacted( user.memberId( ) );
        memberDomainService.identifyMyMobile( member, mobile );
        memberRepository.save( member );
        log.info( "Mobile identified by member[{}].", member.identifier( ) );
    }

    @Transactional
    public void updateMyBaseSetting( UpdateMyBaseSettingCommand command, User user ) {
        rateLimiter.applyFor( user.tenantId( ), "Member:UpdateMySetting", 5 );

        Member member = memberRepository.findByIdExacted( user.memberId( ) );
        ResultWithDomainEvents< Member, MemberDomainEvent > resultWithDomainEvents = member.updateBaseSetting( command.name( ), user );
        Member saved = memberRepository.save( resultWithDomainEvents.result );
        log.info( "Member base setting updated by member[{}].", member.identifier( ) );
        this.domainEventPublisher.publish( saved, resultWithDomainEvents.events );
    }

    @Transactional
    public void updateMyAvatar( UpdateMyAvatarCommand command, User user ) {
        rateLimiter.applyFor( user.tenantId( ), "Member:UpdateMyAvatar", 5 );

        Member member = memberRepository.findByIdExacted( user.memberId( ) );
        member.updateAvatar( command.avatar( ), user );
        memberRepository.save( member );
        log.info( "Avatar updated by member[{}].", member.identifier( ) );
    }

    @Transactional
    public void deleteMyAvatar( User user ) {
        rateLimiter.applyFor( user.tenantId( ), "Member:DeleteMyAvatar", 5 );

        Member member = memberRepository.findByIdExacted( user.memberId( ) );
        member.deleteAvatar( user );
        memberRepository.save( member );
        log.info( "Avatar deleted by member[{}].", member.identifier( ) );
    }

    @Transactional
    public void unbindMyWx( User user ) {
        rateLimiter.applyFor( user.tenantId( ), "Member:UnbindMyWx", 5 );

        Member member = memberRepository.findByIdExacted( user.memberId( ) );
        member.unbindWx( user );
        memberRepository.save( member );
        log.info( "Wx unbound by member[{}].", member.identifier( ) );
    }

    @Transactional
    public void findBackPassword( FindBackPasswordCommand command ) {
        rateLimiter.applyFor( "Member:FindBackPassword:All", 5 );

        String mobileOrEmail = command.mobileOrEmail( );
        verificationCodeChecker.check( mobileOrEmail, command.verification( ), FIND_BACK_PASSWORD );

        Member member = memberRepository.findByMobileOrEmail( mobileOrEmail )
                .orElseThrow( ( ) -> new MryException( MEMBER_NOT_FOUND_FOR_FINDBACK_PASSWORD,
                        "没有找到手机号或密码对应用户。",
                        mapOf( "mobileOrEmail", mobileOrEmail ) ) );

        member.changePassword( passwordEncoder.encode( command.password( ) ), member.toUser( ) );
        memberRepository.save( member );
        log.info( "Password found back by member[{}].", member.identifier( ) );
    }

    @Transactional
    public void topApp( String appid, User user ) {
        rateLimiter.applyFor( user.memberId( ), "Member:TopApp", 5 );

        Member member = memberRepository.findByIdExacted( user.memberId( ) );
        member.topApp( appid, user );
        memberRepository.save( member );
        log.info( "Mark app[{}] as top by member[{}].", appid, member.identifier( ) );
    }

    @Transactional
    public void cancelTopApp( String appid, User user ) {
        rateLimiter.applyFor( user.tenantId( ), "Member:CancelTopApp", 5 );

        Member member = memberRepository.findByIdExacted( user.memberId( ) );
        member.cancelTopApp( appid, user );
        memberRepository.save( member );
        log.info( "Unmark app[{}] as top by member[{}].", appid, member.identifier( ) );
    }
}
