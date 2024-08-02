package io.github.yeahfo.mry.learn.core.app.domain.ui.shadow;

import io.github.yeahfo.mry.learn.core.common.validation.color.Color;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record Shadow(
        @Min( 0 )
        @Max( 100 )
        int width,//边框宽度

        @Color
        String color//阴影颜色
) {
    public static Shadow noShadow( ) {
        return Shadow.builder( ).width( 0 ).color( "rgba(0, 0, 0, 0.1)" ).build( );
    }
}
