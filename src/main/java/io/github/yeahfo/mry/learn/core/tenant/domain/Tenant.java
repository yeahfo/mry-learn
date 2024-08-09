package io.github.yeahfo.mry.learn.core.tenant.domain;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.github.yeahfo.mry.learn.core.common.domain.AggregateRoot;
import io.github.yeahfo.mry.learn.core.common.domain.UploadedFile;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.domain.invoice.InvoiceTitle;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.github.yeahfo.mry.learn.core.order.domain.delivery.Consignee;
import io.github.yeahfo.mry.learn.core.plan.domain.PlanType;
import io.github.yeahfo.mry.learn.core.tenant.domain.event.TenantBaseSettingUpdatedEvent;
import io.github.yeahfo.mry.learn.core.tenant.domain.event.TenantDomainEvent;
import io.github.yeahfo.mry.learn.core.tenant.domain.event.TenantSubdomainUpdatedEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.FORBIDDEN_SUBDOMAIN_PREFIX;
import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.SUBDOMAIN_UPDATED_TOO_OFTEN;
import static io.github.yeahfo.mry.learn.core.common.utils.MapUtils.mapOf;
import static io.github.yeahfo.mry.learn.core.common.utils.SnowflakeIdGenerator.newSnowflakeIdAsString;
import static java.time.Instant.now;
import static java.time.LocalDate.ofInstant;
import static java.time.ZoneId.systemDefault;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class Tenant extends AggregateRoot {
    private static final Set< String > FORBIDDEN_SUBDOMAIN_PREFIXES = Set.of( "www", "ww", "help", "helps", "api", "apis", "image", "images",
            "doc", "docs", "blog", "blogs", "admin", "administrator", "ops", "kibana", "console", "consoles", "manager", "managers",
            "mry", "contact", "contacts", "new", "news", "mail", "mails", "ftp", "ftps", "me", "my", "video", "videos", "tv", "sex", "porn", "naked",
            "official", "gov", "government", "file", "files", "hr", "job", "work", "career", "forum", "m", "static" );
    private String name;//租户名称
    private Packages packages;//当前套餐
    private ResourceUsage resourceUsage;//当前资源使用量统计
    private UploadedFile logo;//Logo
    private UploadedFile loginBackground;//登录背景图片
    private String subdomainPrefix;//子域名前缀
    private boolean subdomainReady;//子域名是否生效
    private String subdomainRecordId;//阿里云返回的DNS记录ID
    private Instant subdomainUpdatedAt;//子域名更新时间
    private ApiSetting apiSetting;//API集成设置
    private boolean active;//用于后台管理端设置，非active时所有成员无法登录，无法访问API
    private InvoiceTitle invoiceTitle;//发票抬头
    private List< Consignee > consignees;//收货人

    public Tenant( String name, User user ) {
        super( user.tenantId( ), user );
        this.name = name;
        this.packages = Packages.init( );
        this.resourceUsage = ResourceUsage.init( );
        this.apiSetting = ApiSetting.init( );
        this.active = true;
        this.consignees = new ArrayList<>( 3 );
        addOpsLog( "注册", user );
    }

    public static String newTenantId( ) {
        return newSnowflakeIdAsString( );
    }

    public Packages packages( ) {
        return packages;
    }

    public PackagesStatus packagesStatus( ) {
        return PackagesStatus.builder( ).id( this.identifier( ) ).packages( this.packages ).resourceUsage( this.resourceUsage ).build( );
    }

    public void updatePlanType( PlanType planType, Instant expireAt, User user ) {
        this.packages.updatePlanType( planType, expireAt );
        addOpsLog( "设置套餐为" + planType.getName( ) + "(" + ofInstant( expireAt, systemDefault( ) ) + "过期)", user );
    }

    public void useSms( ) {
        this.resourceUsage.increaseSmsSentCountForCurrentMonth( );

        if ( this.resourceUsage.getSmsSentCountForCurrentMonth( ) > this.packages.effectiveMaxSmsCountPerMonth( ) ) {
            this.packages.tryUseExtraRemainSms( );
        }
    }

    public String name( ) {
        return name;
    }

    public String subdomainPrefix( ) {
        return subdomainPrefix;
    }

    public boolean subdomainReady( ) {
        return subdomainReady;
    }

    public UploadedFile logo( ) {
        return logo;
    }

    public ResultWithDomainEvents< Tenant, TenantDomainEvent > updateBaseSetting( String name, UploadedFile loginBackground, User user ) {
        this.name = name;
        this.loginBackground = loginBackground;
        addOpsLog( "更新基本设置", user );
        return new ResultWithDomainEvents<>( this, new TenantBaseSettingUpdatedEvent( user ) );
    }

    public ResultWithDomainEvents< Tenant, TenantDomainEvent > updateSubdomain( String newSubdomainPrefix, User user ) {
        if ( isNotBlank( newSubdomainPrefix ) && FORBIDDEN_SUBDOMAIN_PREFIXES.contains( newSubdomainPrefix ) ) {
            throw new MryException( FORBIDDEN_SUBDOMAIN_PREFIX, "不允许使用该子域名。", mapOf( "subdomainPrefix", newSubdomainPrefix ) );
        }

        List< TenantDomainEvent > events = new ArrayList<>( );
        String oldSubdomainPrefix = this.subdomainPrefix;
        if ( !Objects.equals( oldSubdomainPrefix, newSubdomainPrefix ) ) {
            if ( !subdomainUpdatable( ) ) {
                throw new MryException( SUBDOMAIN_UPDATED_TOO_OFTEN,
                        "子域名30天内之内只能更新一次。", mapOf( "subdomainPrefix", newSubdomainPrefix ) );
            }

            this.subdomainPrefix = newSubdomainPrefix;
            this.subdomainReady = false;
            this.subdomainUpdatedAt = now( );
            addOpsLog( "更新域名前缀为" + newSubdomainPrefix, user );
            events.add( new TenantSubdomainUpdatedEvent( oldSubdomainPrefix, newSubdomainPrefix, user ) );
        }
        return new ResultWithDomainEvents<>( this, events );
    }

    public boolean subdomainUpdatable( ) {
        return subdomainUpdatedAt == null || now( ).minusSeconds( 30 * 24 * 3600L ).isAfter( subdomainUpdatedAt );
    }

    public void updateLogo( UploadedFile logo, User user ) {

    }
}
