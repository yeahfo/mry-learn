package io.github.yeahfo.mry.learn.core.tenant;

import io.github.yeahfo.mry.learn.core.common.domain.AggregateRoot;
import io.github.yeahfo.mry.learn.core.common.domain.UploadedFile;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.domain.invoice.InvoiceTitle;
import io.github.yeahfo.mry.learn.core.order.domain.delivery.Consignee;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
//        this.raiseEvent( new TenantCreatedEvent(  user ) );
        addOpsLog( "注册", user );
    }
}
