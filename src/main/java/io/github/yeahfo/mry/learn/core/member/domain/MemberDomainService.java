package io.github.yeahfo.mry.learn.core.member.domain;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.github.yeahfo.mry.learn.common.password.PasswordEncoderFactories;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.github.yeahfo.mry.learn.core.department.domain.DepartmentRepository;
import io.github.yeahfo.mry.learn.core.member.domain.event.MemberDomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.*;
import static io.github.yeahfo.mry.learn.core.common.utils.MapUtils.mapOf;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Component
@RequiredArgsConstructor
public class MemberDomainService {
    private static final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder( );
    private final MemberRepository memberRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional( propagation = REQUIRES_NEW )//使用REQUIRES_NEW保证即便其他地方有异常，这里也能正常写库
    public void recordMemberFailedLogin( Member member ) {
        member.recordFailedLogin( );
        memberRepository.save( member );
    }

    public ResultWithDomainEvents< Member, MemberDomainEvent > updateMember( Member member, String name, List< String > departmentIds, String mobile, String email, User user ) {
        if ( departmentRepository.notAllDepartmentsExist( departmentIds, member.tenantId( ) ) ) {
            throw new MryException( NOT_ALL_DEPARTMENTS_EXITS, "更新成员失败，有部门不存在。", "name", name, "departmentIds", departmentIds );
        }
        if ( isBlank( mobile ) && isBlank( email ) ) {
            throw new MryException( MOBILE_EMAIL_CANNOT_BOTH_EMPTY, "更新成员失败，手机号和邮箱不能同时为空。",
                    "memberId", member.identifier( ) );
        }
        if ( isNotBlank( mobile ) && !mobile.equals( member.mobile( ) ) && memberRepository.existsByMobile( mobile ) ) {
            throw new MryException( MEMBER_WITH_MOBILE_ALREADY_EXISTS, "更新成员失败，手机号已被占用。",
                    mapOf( "memberId", member.identifier( ) ) );
        }

        if ( isNotBlank( email ) && !email.equals( member.email( ) ) && memberRepository.existsByEmail( email ) ) {
            throw new MryException( MEMBER_WITH_EMAIL_ALREADY_EXISTS, "更新成员失败，邮箱已被占用。",
                    mapOf( "memberId", member.identifier( ) ) );
        }
        return member.update( name, departmentIds, mobile, email, user );
    }

    public void checkMinTenantAdminLimit( String tenantId ) {
        int count = ( int ) memberRepository.findTenantCachedMembers( tenantId )
                .stream( )
                .filter( member -> member.isTenantAdmin( ) && member.active( ) )
                .count( );
        if ( count < 1 ) {
            throw new MryException( NO_ACTIVE_TENANT_ADMIN_LEFT, "必须保留至少一个可用的系统管理员。",
                    mapOf( "tenantId", tenantId ) );
        }
    }

    public void checkMaxTenantAdminLimit( String tenantId ) {
        int count = ( int ) memberRepository.findTenantCachedMembers( tenantId )
                .stream( ).filter( TenantCachedMember::isTenantAdmin )
                .count( );
        if ( count > 10 ) {
            throw new MryException( MAX_TENANT_ADMIN_REACHED, "系统管理员数量已超出最大限制（10名）。",
                    mapOf( "tenantId", tenantId ) );
        }
    }

    public void changeMyPassword( Member member, String oldPassword, String newPassword ) {
        if ( Objects.equals( oldPassword, newPassword ) ) {
            throw new MryException( NEW_PASSWORD_SAME_WITH_OLD, "修改密码失败，新密码不能与原密码相同。", "memberId", member.identifier( ) );
        }

        if ( !passwordEncoder.matches( oldPassword, member.password( ) ) ) {
            throw new MryException( PASSWORD_NOT_MATCH, "修改密码失败，原密码不正确。", "memberId", member.identifier( ) );
        }
        member.changePassword( passwordEncoder.encode( newPassword ), member.toUser( ) );
    }

    public void changeMyMobile( Member member, String newMobile, String password ) {
        if ( !passwordEncoder.matches( password, member.password( ) ) ) {
            throw new MryException( PASSWORD_NOT_MATCH, "修改手机号失败，密码不正确。", "memberId", member.identifier( ) );
        }

        if ( Objects.equals( member.mobile( ), newMobile ) ) {
            return;
        }

        if ( memberRepository.existsByMobile( newMobile ) ) {
            throw new MryException( MEMBER_WITH_MOBILE_ALREADY_EXISTS, "修改手机号失败，手机号对应成员已存在。",
                    mapOf( "mobile", newMobile, "memberId", member.identifier( ) ) );
        }
        member.changeMobile( newMobile, member.toUser( ) );
    }

    public void identifyMyMobile( Member member, String mobile ) {
        if ( !Objects.equals( member.mobile( ), mobile ) && memberRepository.existsByMobile( mobile ) ) {
            throw new MryException( MEMBER_WITH_MOBILE_ALREADY_EXISTS, "认证失败，手机号对应成员已存在。",
                    mapOf( "mobile", mobile, "memberId", member.identifier( ), "mobile", mobile ) );
        }
        member.identifyMobile( mobile, member.toUser( ) );
    }
}
