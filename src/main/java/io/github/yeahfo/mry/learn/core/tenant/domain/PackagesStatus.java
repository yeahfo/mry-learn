package io.github.yeahfo.mry.learn.core.tenant.domain;

import lombok.Builder;

@Builder
public record PackagesStatus(
        String id,//租户ID
        Packages packages,//租户当前套餐
        ResourceUsage resourceUsage//租户当前的资源使用量
) {
    public static final int MAX_PLATE_SIZE = 100000000;

    public boolean isMaxMemberReached( ) {
        int maxAllowedMemberCount = packages.effectiveMaxMemberCount( );
        int currentMemberCount = resourceUsage.getMemberCount( );
        return currentMemberCount >= maxAllowedMemberCount;
    }

    public boolean isMaxSmsCountReached( ) {
        if ( resourceUsage.getSmsSentCountForCurrentMonth( ) < packages.effectiveMaxSmsCountPerMonth( ) ) {
            return false;
        }

        return packages.getExtraRemainSmsCount( ) <= 0;
    }
}
