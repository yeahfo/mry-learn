package io.github.yeahfo.mry.learn.core.member.domain;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.github.yeahfo.mry.learn.core.department.domain.DepartmentRepository;
import io.github.yeahfo.mry.learn.core.member.domain.event.MemberCreatedEvent;
import io.github.yeahfo.mry.learn.core.member.domain.event.MemberDepartmentsChangedEvent;
import io.github.yeahfo.mry.learn.core.member.domain.event.MemberDomainEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.*;
import static io.github.yeahfo.mry.learn.core.common.utils.MapUtils.mapOf;
import static java.util.Set.copyOf;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class MemberFactory {
    private final MemberRepository memberRepository;
    private final DepartmentRepository departmentRepository;

    public MemberFactory( MemberRepository memberRepository, DepartmentRepository departmentRepository ) {
        this.memberRepository = memberRepository;
        this.departmentRepository = departmentRepository;
    }

    public ResultWithDomainEvents< Member, MemberDomainEvent > create( String name,
                                                                       List< String > departmentIds,
                                                                       String mobile,
                                                                       String email,
                                                                       String password,
                                                                       User user ) {
        return create( name, departmentIds, mobile, email, password, null, user );
    }

    public ResultWithDomainEvents< Member, MemberDomainEvent > create( String name,
                                                                       List< String > departmentIds,
                                                                       String mobile,
                                                                       String email,
                                                                       String password,
                                                                       String customId,
                                                                       User user ) {
        String tenantId = user.tenantId( );
        if ( departmentRepository.notAllDepartmentsExist( departmentIds, tenantId ) ) {
            throw new MryException( NOT_ALL_DEPARTMENTS_EXITS, "添加成员失败，有部门不存在。", "name", name, "departmentIds", departmentIds );
        }

        if ( isBlank( mobile ) && isBlank( email ) ) {
            throw new MryException( MOBILE_EMAIL_CANNOT_BOTH_EMPTY, "添加成员失败，手机号和邮箱不能同时为空。", "tenantId", tenantId );
        }

        if ( isNotBlank( mobile ) && memberRepository.existsByMobile( mobile ) ) {
            throw new MryException( MEMBER_WITH_MOBILE_ALREADY_EXISTS, "添加成员失败，手机号已被占用。", mapOf( "mobile", mobile ) );
        }

        if ( isNotBlank( email ) && memberRepository.existsByEmail( email ) ) {
            throw new MryException( MEMBER_WITH_EMAIL_ALREADY_EXISTS, "添加成员失败，邮箱已被占用。", mapOf( "email", email ) );
        }

        if ( isNotBlank( customId ) && memberRepository.existsByCustomId( customId, tenantId ) ) {
            throw new MryException( MEMBER_WITH_CUSTOM_ID_ALREADY_EXISTS, "添加成员失败，自定义编号已被占用。", mapOf( "customId", customId ) );
        }
        Member member = new Member( name, departmentIds, mobile, email, password, customId, user );
        List< MemberDomainEvent > domainEvents = new ArrayList<>( );
        domainEvents.add( new MemberCreatedEvent( user ) );
        if ( isNotEmpty( departmentIds ) ) {
            domainEvents.add( new MemberDepartmentsChangedEvent( Set.of( ), copyOf( departmentIds ), user ) );
        }
        return new ResultWithDomainEvents<>( member, domainEvents );
    }
}
