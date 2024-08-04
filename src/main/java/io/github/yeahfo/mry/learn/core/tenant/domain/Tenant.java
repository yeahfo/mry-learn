package io.github.yeahfo.mry.learn.core.tenant.domain;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.github.yeahfo.mry.learn.core.common.domain.AggregateRoot;
import io.github.yeahfo.mry.learn.core.common.domain.UploadedFile;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.domain.invoice.InvoiceTitle;
import io.github.yeahfo.mry.learn.core.order.domain.delivery.Consignee;
import io.github.yeahfo.mry.learn.core.plan.domain.PlanType;
import io.github.yeahfo.mry.learn.core.tenant.domain.event.TenantBaseSettingUpdatedEvent;
import io.github.yeahfo.mry.learn.core.tenant.domain.event.TenantDomainEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static io.github.yeahfo.mry.learn.core.common.utils.SnowflakeIdGenerator.newSnowflakeIdAsString;
import static java.time.LocalDate.ofInstant;
import static java.time.ZoneId.systemDefault;

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
}
