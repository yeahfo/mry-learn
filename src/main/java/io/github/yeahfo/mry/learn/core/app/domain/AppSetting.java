package io.github.yeahfo.mry.learn.core.app.domain;

import io.github.yeahfo.mry.learn.core.app.domain.config.AppConfig;
import io.github.yeahfo.mry.learn.core.app.domain.page.Page;
import io.github.yeahfo.mry.learn.core.common.validation.collection.NoNullElement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.*;

@Builder
public record AppSetting( @Valid
                          @NotNull
                          AppConfig config,//应用整体设置

                          @Valid
                          @NotNull
                          AppTopBar appTopBar,//顶部导航栏（大屏设备）

                          @Valid
                          @NotNull
                          @NotEmpty
                          @NoNullElement
                          @Size( max = MAX_PER_APP_PAGE_SIZE )
                          List< Page > pages,//页面

                          @Valid
                          @NotNull
                          Menu menu,//菜单

                          @Valid
                          @NotNull
                          @NoNullElement
                          @Size( max = MAX_PER_APP_ATTRIBUTE_SIZE )
                          List< Attribute > attributes,//自定义属性

                          @Valid
                          @NotNull
                          @NoNullElement
                          @Size( max = MAX_PER_APP_OPERATION_MENU_SIZE )
                          List< OperationMenuItem > operationMenuItems,//自定义运营菜单

                          @Valid
                          @NotNull
                          PlateSetting plateSetting,//码牌设置

                          @Valid
                          @NotNull
                          CirculationStatusSetting circulationStatusSetting//状态流转
) {
}
