package io.github.yeahfo.mry.learn.members.domain;

import io.github.yeahfo.mry.learn.common.domain.AggregateRoot;
import io.github.yeahfo.mry.learn.common.domain.Role;
import io.github.yeahfo.mry.learn.common.domain.UploadedFile;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;

public class Member extends AggregateRoot {
    private String name;//名字
    private Role role;//角色
    private String mobile;//手机号，全局唯一，与email不能同时为空
    private String email;//邮箱，全局唯一，与mobile不能同时为空
    private String pcWxOpenId;//PC微信扫码登录后获得的openid，全局唯一
    private String mobileWxOpenId;//手机微信授权后获得的openid，全局唯一
    private String wxNickName;//微信昵称
    private String wxUnionId;//微信的Union ID
    private String password;//密码
    private boolean mobileIdentified;//是否已验证手机号
    private IdentityCard identityCard;//身份证
    private UploadedFile avatar;//avatar
    private String customId;//自定义编号，用于API查询用，租户下唯一
    private List< String > topAppIds;//顶置的app
    private FailedLoginCount failedLoginCount;//登录失败次数
    private boolean active;//是否启用
    private boolean tenantActive;//所在Tenant是否启用，通过EDA更新
    private List< String > departmentIds;

    @Builder
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
