package io.github.yeahfo.mry.learn.tenants.domain;

import io.github.yeahfo.mry.learn.common.domain.AggregateRoot;
import io.github.yeahfo.mry.learn.common.domain.InvoiceTitle;
import io.github.yeahfo.mry.learn.common.domain.UploadedFile;
import io.github.yeahfo.mry.learn.order.domain.delivery.Consignee;

import java.time.Instant;
import java.util.List;

public class Tenant extends AggregateRoot {
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
}
