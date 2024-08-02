package io.github.yeahfo.mry.learn.core.app.domain.ui.border;

import io.github.yeahfo.mry.learn.core.common.validation.collection.NoNullElement;
import io.github.yeahfo.mry.learn.core.common.validation.color.Color;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

import static io.github.yeahfo.mry.learn.core.app.domain.ui.border.BorderType.NONE;

@Builder
public record Border( @NotNull
                      BorderType type,//边框类型

                      @Min( 0 )

                      @Max( 100 )
                      int width,//宽度

                      @NotNull
                      @NoNullElement
                      Set< BorderSide > sides,//四边范围

                      @Color
                      String color//颜色
) {
    public static Border noBorder( ) {
        return Border.builder( ).type( NONE ).width( 1 ).sides( Set.of( BorderSide.values( ) ) ).color( "rgba(220, 223, 230, 1)" ).build( );
    }
}
