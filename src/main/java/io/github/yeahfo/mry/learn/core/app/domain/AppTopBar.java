package io.github.yeahfo.mry.learn.core.app.domain;

import io.github.yeahfo.mry.learn.core.app.domain.ui.border.Border;
import io.github.yeahfo.mry.learn.core.app.domain.ui.shadow.Shadow;
import io.github.yeahfo.mry.learn.core.common.validation.color.Color;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

import static io.github.yeahfo.mry.learn.core.app.domain.ui.border.BorderSide.BOTTOM;
import static io.github.yeahfo.mry.learn.core.app.domain.ui.border.BorderType.SOLID;
import static io.github.yeahfo.mry.learn.core.app.domain.ui.shadow.Shadow.noShadow;
import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.MAX_PADDING;
import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.MIN_PADDING;

@Builder
public record AppTopBar( @Min( 30 )
                         @Max( 80 )
                         int height,//高度

                         @Color
                         String textColor,//文本颜色

                         @Color
                         String backgroundColor,//背景颜色

                         @Valid
                         @NotNull
                         Border border,//边框

                         @Valid
                         @NotNull
                         Shadow shadow,//阴影

                         @Min( MIN_PADDING )
                         @Max( MAX_PADDING )
                         int hPadding//水平内边距
) {

    public static AppTopBar defaultAppTopBar( ) {
        return AppTopBar.builder( )
                .height( 50 )
                .textColor( "#606266" )
                .backgroundColor( "rgba(255, 255, 255, 1)" )
                .border( Border.builder( ).type( SOLID ).width( 1 ).sides( Set.of( BOTTOM ) ).color( "#DCDFE6" ).build( ) )
                .shadow( noShadow( ) )
                .hPadding( 24 )
                .build( );
    }
}
