package io.eventuate.messaging.redis.spring.common;

import lombok.Getter;
import org.redisson.api.RedissonClient;

import java.util.Collections;
import java.util.List;

@Getter
public class RedissonClients {
    private final List< RedissonClient > redissonClients;

    public RedissonClients( RedissonClient redissonClient ) {
        this.redissonClients = Collections.singletonList( redissonClient );
    }
}
