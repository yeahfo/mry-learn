package io.eventuate.tram.cdc.connector.configuration;

import io.eventuate.cdc.producer.wrappers.DataProducerFactory;
import io.eventuate.cdc.producer.wrappers.EventuateRedisDataProducerWrapper;
import io.eventuate.coordination.leadership.LeaderSelectorFactory;
import io.eventuate.local.common.PublishingFilter;
import io.eventuate.messaging.redis.spring.common.EventuateMessagingRedissonClientsConfiguration;
import io.eventuate.messaging.redis.spring.common.RedissonClients;
import io.eventuate.messaging.redis.spring.leadership.RedisLeaderSelector;
import io.eventuate.messaging.redis.spring.producer.EventuateRedisProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;

// Override to reuse Spring Redis or Redisson starter, supports passwords.
@Configuration
@Profile( "Redis" )
@Import( EventuateMessagingRedissonClientsConfiguration.class )
public class RedisMessageTableChangesToDestinationsConfiguration {
    @Bean
    public PublishingFilter redisDuplicatePublishingDetector( ) {
        return ( fileOffset, topic ) -> true;
    }

    @Bean
    public DataProducerFactory redisDataProducerFactory( RedisTemplate< String, String > redisTemplate ) {
        return ( ) -> new EventuateRedisDataProducerWrapper( new EventuateRedisProducer( redisTemplate, 2 ) );
    }

    @Bean
    @ConditionalOnMissingBean
    public LeaderSelectorFactory connectorLeaderSelectorFactory( RedissonClients redissonClients ) {
        return ( lockId, leaderId, leaderSelectedCallback, leaderRemovedCallback ) ->
                new RedisLeaderSelector( redissonClients, lockId, leaderId, 10000, leaderSelectedCallback, leaderRemovedCallback );
    }
}
