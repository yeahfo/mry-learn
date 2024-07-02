package io.github.yeahfo.mry.learn.plans.domain;

import io.github.yeahfo.mry.learn.app.domain.page.control.ControlType;
import lombok.Builder;

import java.util.Set;

@Builder
public record Plan(
        PlanType type,
        int maxAppCount,//应用数量
        int maxQrCount,//实例总量
        int maxSubmissionCount,//提交总量
        int maxDepartmentCount,//部门总量
        int maxGroupCountPerApp,//单个应用下group数量
        int maxMemberCount,//成员数量
        float maxStorage,//上传文件容量，单位GB
        int maxSmsCountPerMonth,//每月短信用量
        int maxVideoTrafficPerMonth,//每月最大视频播放流量，单位GB

        Set< ControlType > supportedControlTypes,//可用的控件类型
        boolean hideBottomMryLogo,//去除页面底部码如云标识
        boolean hideAds,//去除广告
        boolean videoAudioAllowed,//是否支持上传音视频（用于文件上传控件等）
        boolean customSubdomainAllowed,//自定义子域名
        boolean customLogoAllowed,//自定义logo
        boolean developerAllowed,//启用API开发，包括API调用和webhook
        boolean reportingAllowed,//启用报表功能
        boolean kanbanAllowed,//状态看板
        boolean submissionNotifyAllowed,//提交提醒功能
        boolean batchImportQrAllowed,//批量导入实例数据
        boolean batchImportMemberAllowed,//批量导入成员数据
        boolean submissionApprovalAllowed,//提交审批
        boolean assignmentAllowed//任务管理
) {
}
