package io.github.yeahfo.mry.learn.tenants.domain;

import io.github.yeahfo.mry.learn.plans.domain.Plan;

import java.time.Instant;

public record Packages(
        Plan plan,//当前的套餐方案
        Instant expireAt,//过期时间
        String planVersion,//当前套餐版本
        int extraMemberCount,//增购成员数量，永久生效
        int extraStorage,//增购存储容量，永久生效
        int extraRemainSmsCount,//增购短信量，永久生效
        float extraRemainVideoTraffic//剩余增购容量(单位GB)，永久生效
) {
}
