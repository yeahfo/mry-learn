package io.github.yeahfo.mry.learn.core.common.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties( "mry.common" )
public record CommonProperties( boolean httpsEnabled,
                                @NotBlank
                                String baseDomainName,
                                boolean limitRate,
                                @NotBlank
                                String webhookUserName,
                                @NotBlank
                                String webhookPassword ) {
}
