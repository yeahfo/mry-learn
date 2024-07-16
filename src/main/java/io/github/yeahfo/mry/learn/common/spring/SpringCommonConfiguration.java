package io.github.yeahfo.mry.learn.common.spring;

import io.github.yeahfo.mry.learn.core.common.utils.CustomizedObjectMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableAsync
@EnableRetry
@EnableCaching
@Configuration
public class SpringCommonConfiguration {

    @Bean
    CustomizedObjectMapper objectMapper( ) {
        return new CustomizedObjectMapper( );
    }
}
