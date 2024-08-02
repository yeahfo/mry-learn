package io.github.yeahfo.mry.learn.core.app.domain.page;

import io.github.yeahfo.mry.learn.core.common.utils.Identified;
import io.github.yeahfo.mry.learn.core.common.validation.id.page.PageId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.MAX_PER_PAGE_CONTROL_SIZE;

@Builder
public record Page( @PageId
                    @NotBlank
                    String id,//页面ID

                    @Valid
                    @NotNull
                    PageHeader header,//页眉

                    @Valid
                    @NotNull
                    PageTitle title,//页面标题

                    @Valid
                    @NotNull
                    @NoNullElement
                    @Size( max = MAX_PER_PAGE_CONTROL_SIZE )
                    List< Control > controls,//控件列表

                    @Valid
                    @NotNull
                    SubmitButton submitButton,//提交按钮

                    @Valid
                    @NotNull
                    PageSetting setting//页面设置
) implements Identified {
    @Override
    public String identifier( ) {
        return id;
    }
}
