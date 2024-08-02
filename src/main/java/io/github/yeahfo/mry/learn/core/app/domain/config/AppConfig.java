package io.github.yeahfo.mry.learn.core.app.domain.config;

import io.github.yeahfo.mry.learn.core.common.domain.UploadedFile;
import io.github.yeahfo.mry.learn.core.common.domain.permission.Permission;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.github.yeahfo.mry.learn.core.common.validation.collection.NoNullElement;
import io.github.yeahfo.mry.learn.core.common.validation.id.page.PageId;
import io.github.yeahfo.mry.learn.core.common.validation.nospace.NoSpace;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;
import java.util.Set;

import static io.github.yeahfo.mry.learn.core.common.domain.permission.Permission.*;
import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.*;
import static io.github.yeahfo.mry.learn.core.common.utils.MapUtils.mapOf;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.length;

@Builder
public record AppConfig( @PageId
                         @NotBlank
                         String homePageId,//首页ID

                         @Valid
                         UploadedFile icon,//应用的ICON，显示在我的应用页面

                         @NoSpace
                         @Size( max = 5 )
                         String instanceAlias,//qr称号，用于替换在管理后台显示的列表名称等处

                         @NoSpace
                         @Size( max = 5 )
                         String groupAlias,//group称号，用于替换在管理后台显示的group名称等处

                         @NoSpace
                         @Size( max = 10 )
                         String customIdAlias,//customId称号，用于替换在管理后台显示的自定义编号等处

                         @NotNull
                         Permission operationPermission,//运营权限

                         AppLandingPageType landingPageType,//进入应用时显示的页面

                         @NotNull
                         @NoNullElement
                         @Size( max = 5 )
                         List< QrWebhookType > qrWebhookTypes,

                         boolean allowDuplicateInstanceName,//是否允许qr重名
                         boolean geolocationEnabled,//是否启用qr定位功能
                         boolean plateBatchEnabled,//是否启用批量码牌功能
                         boolean appManualEnabled,//是否启用使用手册功能
                         boolean assignmentEnabled,//是否启用任务管理功能
                         boolean qrIntroductionEnabled,//是否启用实例简介功能
                         boolean qrCustomIdManualEditable//是否启用实例自定义编号的手动编辑功能
) {
    private static final Set< Permission > ALLOWED_OPERATION_PERMISSIONS = Set.of(
            CAN_MANAGE_GROUP,
            CAN_MANAGE_APP,
            AS_GROUP_MEMBER,
            AS_TENANT_MEMBER );

    private static final Set< String > FORBIDDEN_GROUP_ALIAS = Set.of( "应用", "账户" );
    private static final Set< String > FORBIDDEN_INSTANCE_ALIAS = Set.of( "应用", "账户", "分组", "页面", "表单", "属性", "提交", "控件", "成员", "码牌" );
    private static final int MIN_ALIAS_LENGTH = 2;

    public void validate( ) {
        if ( !ALLOWED_OPERATION_PERMISSIONS.contains( operationPermission ) ) {
            throw new MryException( OPERATION_PERMISSION_NOT_ALLOWED, "运营权限不支持。",
                    mapOf( "operationPermission", operationPermission ) );
        }

        if ( isNotBlank( instanceAlias ) && length( instanceAlias ) < MIN_ALIAS_LENGTH ) {
            throw new MryException( INSTANCE_ALIAS_TOO_SHORT, "实例称号太短，不能少于2个字符。" );
        }

        if ( isNotBlank( groupAlias ) && length( groupAlias ) < MIN_ALIAS_LENGTH ) {
            throw new MryException( GROUP_ALIAS_TOO_SHORT, "分组称号太短，不能少于2个字符。" );
        }

        if ( isNotBlank( customIdAlias ) && length( customIdAlias ) < MIN_ALIAS_LENGTH ) {
            throw new MryException( CUSTOM_ID_ALIAS_TOO_SHORT, "自定义ID称号太短，不能少于2个字符。" );
        }

        if ( isNotBlank( instanceAlias ) && FORBIDDEN_INSTANCE_ALIAS.contains( instanceAlias ) ) {
            throw new MryException( INSTANCE_ALIAS_NOT_ALLOWED, "不允许的实例称号：" + instanceAlias + "。" );
        }

        if ( isNotBlank( groupAlias ) && FORBIDDEN_GROUP_ALIAS.contains( groupAlias ) ) {
            throw new MryException( GROUP_ALIAS_NOT_ALLOWED, "不允许的分组称号：" + groupAlias + "。" );
        }
    }
}