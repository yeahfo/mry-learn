package io.github.yeahfo.mry.learn.core.tenant.domain;

import lombok.Builder;

import static io.github.yeahfo.mry.learn.core.common.utils.UuidGenerator.newShortUuid;

@Builder
public class ApiSetting {
    private final String apiKey;
    private String apiSecret;

    public static ApiSetting init( ) {
        return ApiSetting.builder( ).apiKey( newShortUuid( ) ).apiSecret( newShortUuid( ) ).build( );
    }

    public void refreshApiSecret( ) {
        this.apiSecret = newShortUuid( );
    }
}
