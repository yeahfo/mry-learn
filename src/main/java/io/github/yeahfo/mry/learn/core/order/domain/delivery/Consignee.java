package io.github.yeahfo.mry.learn.core.order.domain.delivery;

import io.github.yeahfo.mry.learn.core.common.domain.Address;
import io.github.yeahfo.mry.learn.core.common.utils.Identified;
import io.github.yeahfo.mry.learn.core.common.validation.id.shoruuid.ShortUuid;
import io.github.yeahfo.mry.learn.core.common.validation.mobile.Mobile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.MAX_GENERIC_NAME_LENGTH;

@Builder
public record Consignee( @NotBlank
                         @ShortUuid
                         String id,
                         @NotBlank
                         @Size( max = MAX_GENERIC_NAME_LENGTH )
                         String name,
                         @Mobile
                         @NotBlank
                         String mobile,

                         @Valid
                         @NotNull
                         Address address ) implements Identified {
    @Override
    public Object identifier( ) {
        return id;
    }
}
