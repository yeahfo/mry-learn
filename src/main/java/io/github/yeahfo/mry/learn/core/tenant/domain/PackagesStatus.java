package io.github.yeahfo.mry.learn.core.tenant.domain;

import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import lombok.Builder;

import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.BATCH_MEMBER_IMPORT_NOT_ALLOWED;
import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.MEMBER_COUNT_LIMIT_REACHED;
import static io.github.yeahfo.mry.learn.core.common.utils.MapUtils.mapOf;

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

    public void validateAddMember( ) {
        if ( isMaxMemberReached( ) ) {
            if ( isExpired( ) ) {
                throw new MryException( MEMBER_COUNT_LIMIT_REACHED,
                        "当前套餐(" + currentPlanName( ) + ")已过期，且成员总数已达免费版上限，无法继续添加成员，如需添加请及时续费或升级。",
                        mapOf( "tenantId", tenantId( ) ) );
            }
            throw new MryException( MEMBER_COUNT_LIMIT_REACHED,
                    "无法继续添加成员，成员总数已经达到当前套餐(" + currentPlanName( ) + ")上限，如需添加请及时升级或增购成员数量。",
                    mapOf( "tenantId", tenantId( ) ) );
        }
    }

    private String tenantId( ) {
        return id;
    }

    private boolean isExpired( ) {
        return packages.isExpired( );
    }

    private String currentPlanName( ) {
        return packages.currentPlanName( );
    }

    public void validateImportMember( ) {
        if ( !this.packages.batchImportMemberAllowed( ) ) {
            if ( isExpired( ) ) {
                throw new MryException( BATCH_MEMBER_IMPORT_NOT_ALLOWED,
                        "当前套餐(" + currentPlanName( ) + ")已过期，有效套餐已降为免费版，无法使用批量导入功能，请及时续费或升级。",
                        mapOf( "tenantId", tenantId( ) ) );
            }
            throw new MryException( BATCH_MEMBER_IMPORT_NOT_ALLOWED,
                    "当前套餐(" + currentPlanName( ) + ")无法使用批量导入功能，请及时升级。",
                    mapOf( "tenantId", tenantId( ) ) );
        }

    }

    public int validateImportMembers( ) {
        int maxAllowedMemberCount = packages.effectiveMaxMemberCount( );
        int currentMemberCount = resourceUsage.getMemberCount( );

        if ( currentMemberCount >= maxAllowedMemberCount ) {
            if ( isExpired( ) ) {
                throw new MryException( MEMBER_COUNT_LIMIT_REACHED,
                        "上传失败，当前套餐(" + currentPlanName( ) + ")已过期，有效套餐已降为免费版，且当前成员总数已达免费版上限(" + maxAllowedMemberCount + ")，无法继续上传，如需继续请及时续费或升级。",
                        mapOf( "tenantId", tenantId( ) ) );
            }
            throw new MryException( MEMBER_COUNT_LIMIT_REACHED,
                    "上传失败，成员总数已达当前套餐(" + currentPlanName( ) + ")上限(" + maxAllowedMemberCount + ")，如需继续请及时升级。",
                    mapOf( "tenantId", tenantId( ) ) );
        }
        return maxAllowedMemberCount - currentMemberCount;//返回可上传数量
    }
}
