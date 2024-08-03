package io.eventuate.messaging.redis.spring.common;

import lombok.Getter;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RedissonClients {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private RedisServers redisServers;
    @Getter
    private List<RedissonClient> redissonClients;

    public RedissonClients( RedissonClient redissonClient ) {
        this.redissonClients = Collections.singletonList( redissonClient );
    }

    public RedissonClients( RedisServers redisServers) {
        this.redisServers = redisServers;

        redissonClients = redisServers
                .getHostsAndPorts()
                .stream()
                .map(this::createRedissonClient)
                .collect(Collectors.toList());
    }

    private RedissonClient createRedissonClient(RedisServers.HostAndPort hostAndPort) {
        logger.info("Creating redisson client");
        Config config = new Config();
        config.useSingleServer().setRetryAttempts(Integer.MAX_VALUE);
        config.useSingleServer().setRetryInterval(100);
        config.useSingleServer().setAddress(String.format("redis://%s:%s", hostAndPort.getHost(), hostAndPort.getPort()));
        logger.info("Created redisson client");
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
