package io.github.yeahfo.mry.learn.order.domain.delivery;

import io.github.yeahfo.mry.learn.common.domain.Address;
import io.github.yeahfo.mry.learn.common.domain.Identified;
import io.github.yeahfo.mry.learn.common.validation.mobile.Mobile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record Consignee( @NotBlank
                         String id,
                         @NotBlank
                         @Size( max = 50 )
                         String name,
                         @Mobile
                         @NotBlank
                         String mobile,
                         @Valid
                         @NotNull
                         Address address ) implements Identified {
    @Override
    public String getIdentifier( ) {
        return id;
    }
}
