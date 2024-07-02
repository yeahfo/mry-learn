package io.github.yeahfo.mry.learn.tenants.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import static io.github.yeahfo.mry.learn.common.utils.UuidGenerator.newShortUuid;
import static lombok.AccessLevel.PRIVATE;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor( access = PRIVATE )
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
