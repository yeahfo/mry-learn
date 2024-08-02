package io.github.yeahfo.mry.learn.core.app.domain;

import io.github.yeahfo.mry.learn.core.common.domain.AggregateRoot;
import io.github.yeahfo.mry.learn.core.common.domain.UploadedFile;

import java.security.Permission;
import java.util.List;
import java.util.Map;

public class App extends AggregateRoot {
    protected String name;//应用的名称
    protected UploadedFile icon;//图标，保存时从setting同步
    protected boolean active;//是否启用
    protected boolean locked;//是否锁定，锁定之后无法编辑，但是可以正常使用
    protected List<String> managers;//应用管理员
    protected AppSetting setting;//应用设置
    protected String version;//setting所对应的版本，用于保证在多人同时编辑时始终基于最新版本进行更新
    protected Permission permission;//App的permission，由所有页面的的最小权限而来
    protected Permission operationPermission;//运营所需权限，保存时从AppConfig同步而来
    protected IndexedFieldRegistry attributeIndexedValueRegistry;//attributeId -> field，属性值索引字段注册表，保存App时自动构建
    protected Map<String, IndexedFieldRegistry> controlIndexedValueRegistries;//pageId -> registry(controlId -> field)，控件答案索引字段注册表，保存App时自动构建
    protected boolean hasWeeklyResetAttributes;//是否存在有每周初需要重新计算的自定义属性
    protected boolean hasMonthlyResetAttributes;//是否存在有每月初需要重新计算的自定义属性
    protected boolean hasSeasonlyResetAttributes;//是否存在有每季度初需要重新计算的自定义属性
    protected boolean hasYearlyResetAttributes;//是否存在有每年初需要重新计算的自定义属性
    protected ReportSetting reportSetting;//报告设置
    protected WebhookSetting webhookSetting;//Webhook配置
    protected String sourceAppId;//从哪个App拷贝来的
    protected String appTemplateId;//从哪个应用模板而来的
    protected boolean groupSynced;//分组是否从部门同步
}
