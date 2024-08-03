package io.github.yeahfo.mry.learn.core.member.domain;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.github.yeahfo.mry.learn.core.common.domain.AggregateRoot;
import io.github.yeahfo.mry.learn.core.common.domain.Role;
import io.github.yeahfo.mry.learn.core.common.domain.UploadedFile;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.github.yeahfo.mry.learn.core.member.domain.event.MemberDeletedEvent;
import io.github.yeahfo.mry.learn.core.member.domain.event.MemberDepartmentsChangedEvent;
import io.github.yeahfo.mry.learn.core.member.domain.event.MemberDomainEvent;
import io.github.yeahfo.mry.learn.core.member.domain.event.MemberNameChangedEvent;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static io.github.yeahfo.mry.learn.core.common.domain.Role.TENANT_ADMIN;
import static io.github.yeahfo.mry.learn.core.common.domain.Role.TENANT_MEMBER;
import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.*;
import static io.github.yeahfo.mry.learn.core.common.utils.MapUtils.mapOf;
import static io.github.yeahfo.mry.learn.core.common.utils.SnowflakeIdGenerator.newSnowflakeIdAsString;
import static io.github.yeahfo.mry.learn.core.common.utils.UuidGenerator.newShortUuid;
import static java.time.LocalDate.now;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


public class Member extends AggregateRoot {
    private static final String WX_HEAD_IMAGE = "WX_HEAD_IMAGE";

    protected String name;//名字
    protected Role role;//角色
    protected String mobile;//手机号，全局唯一，与email不能同时为空
    protected String email;//邮箱，全局唯一，与mobile不能同时为空
    protected String pcWxOpenId;//PC微信扫码登录后获得的openid，全局唯一
    protected String mobileWxOpenId;//手机微信授权后获得的openid，全局唯一
    protected String wxNickName;//微信昵称
    protected String wxUnionId;//微信的Union ID
    protected String password;//密码
    protected boolean mobileIdentified;//是否已验证手机号
    protected IdentityCard identityCard;//身份证
    protected UploadedFile avatar;//avatar
    protected String customId;//自定义编号，用于API查询用，租户下唯一
    protected List< String > topAppIds;//顶置的app
    protected FailedLoginCount failedLoginCount;//登录失败次数
    protected boolean active;//是否启用
    protected boolean tenantActive;//所在Tenant是否启用，通过EDA更新
    protected List< String > departmentIds;

    protected Member( ) {
    }

    public Member( String mobile, String email, String password, User user ) {
        super( user.memberId( ), user );
        this.name = user.name( );
        this.role = TENANT_ADMIN;
        this.mobile = mobile;
        if ( isNotBlank( this.mobile ) ) {
            this.mobileIdentified = true;
        }
        this.email = email;
        this.password = password;
        this.failedLoginCount = FailedLoginCount.init( );
        this.active = true;
        this.tenantActive = true;
        this.topAppIds = List.of( );
        this.departmentIds = List.of( );
        this.addOpsLog( "注册", user );
    }

    //正常添加
    public Member( String name, List< String > departmentIds, String mobile, String email, String password, String customId, User user ) {
        super( newMemberId( ), user );
        this.name = name;
        this.mobile = mobile;
        this.mobileIdentified = false;
        this.email = email;
        this.password = password;
        this.customId = customId;
        this.role = TENANT_MEMBER;
        this.failedLoginCount = FailedLoginCount.init( );
        this.active = true;
        this.tenantActive = true;
        this.topAppIds = List.of( );
        this.departmentIds = isNotEmpty( departmentIds ) ? departmentIds : new ArrayList<>( 0 );
        this.addOpsLog( "新建", user );
    }

    public static String newMemberId( ) {
        return newSnowflakeIdAsString( );
    }

    public String name( ) {
        return name;
    }

    public String mobile( ) {
        return mobile;
    }

    public String email( ) {
        return email;
    }

    public void checkActive( ) {
        if ( this.failedLoginCount.isLocked( ) ) {
            throw new MryException( MEMBER_ALREADY_LOCKED, "当前用户已经被锁定，次日零点系统将自动解锁。", mapOf( "memberId", this.identifier( ) ) );
        }

        if ( !this.active ) {
            throw new MryException( MEMBER_ALREADY_DEACTIVATED, "当前用户已经被禁用。", mapOf( "memberId", this.identifier( ) ) );
        }

        if ( !this.tenantActive ) {
            throw new MryException( TENANT_ALREADY_DEACTIVATED, "当前账户已经被禁用。",
                    mapOf( "memberId", this.identifier( ), "tenantId", this.tenantId( ) ) );
        }
    }

    public Role role( ) {
        return role;
    }

    public String password( ) {
        return password;
    }

    public void recordFailedLogin( ) {
        this.failedLoginCount.recordFailedLogin( );
    }

    public User toUser( ) {
        return User.humanUser( id, name, tenantId, role );
    }

    public void bindMobileWx( String wxUnionId, String mobileWxOpenId, User user ) {
        this.wxUnionId = wxUnionId;
        this.mobileWxOpenId = mobileWxOpenId;
        addOpsLog( "绑定手机微信:" + wxUnionId, user );
    }

    public boolean updateMobileWxInfo( String mobileWxOpenId, String nickname, String avatarImageUrl, User user ) {
        AtomicBoolean updated = new AtomicBoolean( false );
        Optional.ofNullable( mobileWxOpenId ).filter( when -> !Objects.equals( when, this.mobileWxOpenId ) ).ifPresent( then -> {
            this.mobileWxOpenId = then;
            updated.set( true );
        } );
        updateWxNickNameAndAvatar( nickname, avatarImageUrl, updated );
        if ( updated.get( ) ) {
            this.addOpsLog( "更新手机微信信息", user );
        }
        return updated.get( );
    }

    private String avatarImageUrl( ) {
        return this.avatar != null ? this.avatar.fileUrl( ) : null;
    }

    private UploadedFile wxAvatarOf( String avatarImageUrl ) {
        return UploadedFile.builder( )
                .id( newShortUuid( ) )
                .name( WX_HEAD_IMAGE )
                .type( "image/jpeg" )
                .fileUrl( avatarImageUrl )
                .size( 100 )
                .build( );
    }

    public void bindPcWx( String wxUnionId, String pcWxOpenId, User user ) {
        this.wxUnionId = wxUnionId;
        this.pcWxOpenId = pcWxOpenId;
        addOpsLog( "绑定PC微信:" + wxUnionId, user );
    }

    public boolean updatePcWxInfo( String pcWxOpenId, String nickname, String avatarImageUrl, User user ) {
        AtomicBoolean updated = new AtomicBoolean( false );
        Optional.ofNullable( pcWxOpenId ).filter( when -> !Objects.equals( when, this.pcWxOpenId ) ).ifPresent( then -> {
            this.pcWxOpenId = then;
            updated.set( true );
        } );
        updateWxNickNameAndAvatar( nickname, avatarImageUrl, updated );
        if ( updated.get( ) ) {
            this.addOpsLog( "更新PC微信信息", user );
        }
        return updated.get( );
    }

    private void updateWxNickNameAndAvatar( String nickname, String avatarImageUrl, AtomicBoolean updated ) {
        Optional.ofNullable( nickname ).filter( when -> !Objects.equals( when, this.wxNickName ) ).ifPresent( then -> {
            this.wxNickName = then;
            updated.set( true );
        } );
        Optional.ofNullable( avatarImageUrl ).filter( when -> !Objects.equals( when, this.avatarImageUrl( ) ) ).ifPresent( then -> {
            this.avatar = this.wxAvatarOf( then );
            updated.set( true );
        } );
    }

    public ResultWithDomainEvents< Member, MemberDomainEvent > update( String name, List< String > departmentIds, String mobile, String email, User user ) {
        List< MemberDomainEvent > events = new ArrayList<>( );
        if ( !Objects.equals( this.name, name ) ) {
            this.name = name;
            events.add( new MemberNameChangedEvent( name, user ) );
        }
        if ( !Objects.equals( this.mobile, mobile ) ) {
            this.mobileIdentified = false;
        }
        this.mobile = mobile;
        this.email = email;
        if ( departmentIds != null ) {//传入null时，不做任何departmentIds的更新，主要用于不因为null而将已有的departmentIds更新没了
            Set< String > removedDepartmentIds = diff( this.departmentIds, departmentIds );
            Set< String > addedDepartmentIds = diff( departmentIds, this.departmentIds );
            if ( isNotEmpty( removedDepartmentIds ) || isNotEmpty( addedDepartmentIds ) ) {
                events.add( new MemberDepartmentsChangedEvent( removedDepartmentIds, addedDepartmentIds, user ) );
            }
            this.departmentIds = departmentIds;
        }
        this.addOpsLog( "更新信息", user );
        return new ResultWithDomainEvents<>( this, events );
    }

    private Set< String > diff( List< String > list1, List< String > list2 ) {
        HashSet< String > result = new HashSet<>( list1 );
        result.removeAll( new HashSet<>( list2 ) );
        return result;
    }

    public ResultWithDomainEvents< Member, MemberDomainEvent > updateRole( Role role, User user ) {
        this.role = role;
        this.addOpsLog( "更新角色为" + role.getRoleName( ), user );
        return new ResultWithDomainEvents<>( this );
    }

    public ResultWithDomainEvents< Member, MemberDomainEvent > onDelete( User user ) {
        return new ResultWithDomainEvents<>( this, new MemberDeletedEvent( user ) );
    }

    public ResultWithDomainEvents< Member, MemberDomainEvent > activate( User user ) {
        if ( active ) {
            return new ResultWithDomainEvents<>( this );
        }

        this.active = true;
        addOpsLog( "启用", user );
        return new ResultWithDomainEvents<>( this );
    }

    public ResultWithDomainEvents< Member, MemberDomainEvent > deactivate( User user ) {
        if ( !active ) {
            return new ResultWithDomainEvents<>( this );
        }
        this.active = false;
        addOpsLog( "禁用", user );
        return new ResultWithDomainEvents<>( this );
    }

    public ResultWithDomainEvents< Member, MemberDomainEvent > changePassword( String password, User user ) {
        if ( Objects.equals( this.password, password ) ) {
            return new ResultWithDomainEvents<>( this );
        }
        this.password = password;
        this.addOpsLog( "重置密码", user );
        return new ResultWithDomainEvents<>( this );
    }

    public void unbindWx( User user ) {
        //解绑时同时解绑手机端和PC端
        this.mobileWxOpenId = null;
        this.pcWxOpenId = null;
        this.wxUnionId = null;
        this.wxNickName = null;
        if ( isAvatarFromWx( ) ) {
            this.avatar = null;
        }
        this.addOpsLog( "解绑微信", user );
    }

    private boolean isAvatarFromWx( ) {
        return this.avatar != null && Objects.equals( this.avatar.name( ), WX_HEAD_IMAGE );
    }

    public void changeMobile( String mobile, User user ) {
        if ( Objects.equals( this.mobile, mobile ) ) {
            return;
        }

        this.mobile = mobile;
        this.mobileIdentified = true;
        this.addOpsLog( "修改手机号为[" + mobile + "]", user );
    }

    public void identifyMobile( String mobile, User user ) {
        if ( isNotBlank( this.mobile ) && !Objects.equals( this.mobile, mobile ) ) {
            throw new MryException( IDENTIFY_MOBILE_NOT_THE_SAME, "认证手机号与您当前账号的手机号不一致，无法完成认证。", "mobile", mobile );
        }

        this.mobile = mobile;
        this.mobileIdentified = true;
        this.addOpsLog( "认证手机号：" + mobile, user );
    }

    public ResultWithDomainEvents< Member, MemberDomainEvent > updateBaseSetting( String name, User user ) {
        if ( Objects.equals( this.name, name ) ) {
            return new ResultWithDomainEvents<>( this );
        }

        this.name = name;
        this.addOpsLog( "更新基本设置", user );
        return new ResultWithDomainEvents<>( this, new MemberNameChangedEvent( name, user ) );
    }

    public void updateAvatar( UploadedFile avatar, User user ) {
        this.avatar = avatar;
        addOpsLog( "更新头像", user );
    }

    public void deleteAvatar( User user ) {
        this.avatar = null;
        addOpsLog( "删除头像", user );
    }

    public void topApp( String appid, User user ) {
        topAppIds = Stream.concat( Stream.of( appid ), this.topAppIds.stream( ) ).limit( 20 ).collect( toImmutableList( ) );
        addOpsLog( "顶置应用[" + appid + "]", user );
    }

    public void cancelTopApp( String appid, User user ) {
        this.topAppIds = this.topAppIds.stream( ).filter( id -> !Objects.equals( id, appid ) ).collect( toImmutableList( ) );
        addOpsLog( "取消顶置应用[" + appid + "]", user );
    }

    public UploadedFile avatar( ) {
        return avatar;
    }

    public List< String > toppedAppIds( ) {
        return topAppIds;
    }

    public boolean mobileIdentified( ) {
        return mobileIdentified;
    }

    @Getter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor( access = PRIVATE )
    @AllArgsConstructor( access = PRIVATE )
    public static class FailedLoginCount {

        private static final int MAX_ALLOWED_FAILED_LOGIN_PER_DAY = 30;

        private LocalDate date;
        private int count;

        public static FailedLoginCount init( ) {
            return FailedLoginCount.builder( ).date( now( ) ).count( 0 ).build( );
        }

        private void recordFailedLogin( ) {
            LocalDate now = now( );
            if ( now.equals( date ) ) {
                count++;
            } else {
                this.date = now;
                this.count = 0;
            }
        }

        private boolean isLocked( ) {
            return now( ).equals( date ) && this.count >= MAX_ALLOWED_FAILED_LOGIN_PER_DAY;
        }
    }
}
