package io.eventuate.messaging.redis.spring.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Resolve stingRedisTemplate conflict: io.eventuate.messaging.redis.spring.common.CommonRedisConfiguration#redisTemplate(org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory)
 */
@Configuration
public class CommonRedisConfiguration {

    @Bean
    public RedisConfigurationProperties redisConfigurationProperties() {
        return new RedisConfigurationProperties();
    }

    @Bean
    public RedisServers redisServers(RedisConfigurationProperties redisConfigurationProperties) {
        return new RedisServers(redisConfigurationProperties.getServers());
    }

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory(RedisServers redisServers) {
        RedisServers.HostAndPort mainServer = redisServers.getHostsAndPorts().getFirst( );
        return new LettuceConnectionFactory(mainServer.getHost(), mainServer.getPort());
    }

    @Bean
    @Primary
    public RedisTemplate<String, String> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);
        template.setDefaultSerializer(stringRedisSerializer);
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        return template;
    }

    @Bean
    public RedissonClients redissonClients(RedisServers redisServers) {
        return new RedissonClients(redisServers);
    }
}
