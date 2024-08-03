package io.eventuate.messaging.redis.spring.common;

import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

// Override to reuse Spring Redis or Redisson starter, supports passwords.
@Configuration
@Import( RedissonAutoConfiguration.class )
public class EventuateMessagingRedissonClientsConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public RedissonClients redissonClients( RedissonClient redissonClient ) {
        return new RedissonClients( redissonClient );
    }

}
