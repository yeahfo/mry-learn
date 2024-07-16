package io.github.yeahfo.mry.learn.core.member.domain;

import io.github.yeahfo.mry.learn.core.common.domain.AggregateRoot;
import io.github.yeahfo.mry.learn.core.common.domain.Role;
import io.github.yeahfo.mry.learn.core.common.domain.UploadedFile;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.github.yeahfo.mry.learn.core.common.domain.Role.TENANT_ADMIN;
import static io.github.yeahfo.mry.learn.core.common.domain.Role.TENANT_MEMBER;
import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.*;
import static io.github.yeahfo.mry.learn.core.common.utils.SnowflakeIdGenerator.newSnowflakeIdAsString;
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

    public void checkActive( ) {
        if ( this.failedLoginCount.isLocked( ) ) {
            throw new MryException( MEMBER_ALREADY_LOCKED, "当前用户已经被锁定，次日零点系统将自动解锁。", Map.of( "memberId", this.identifier( ) ) );
        }

        if ( !this.active ) {
            throw new MryException( MEMBER_ALREADY_DEACTIVATED, "当前用户已经被禁用。", Map.of( "memberId", this.identifier( ) ) );
        }

        if ( !this.tenantActive ) {
            throw new MryException( TENANT_ALREADY_DEACTIVATED, "当前账户已经被禁用。",
                    Map.of( "memberId", this.identifier( ), "tenantId", this.tenantId( ) ) );
        }
    }

    public Role role( ) {
        return role;
    }

    @Getter
    @Builder
    @EqualsAndHashCode
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
