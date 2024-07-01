package io.github.yeahfo.mry.learn.core.common.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties( {
        CommonProperties.class
} )
public class PropertiesConfiguration {
}
