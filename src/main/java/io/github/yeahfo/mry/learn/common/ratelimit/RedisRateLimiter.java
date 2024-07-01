package io.github.yeahfo.mry.learn.common.ratelimit;

import io.github.yeahfo.mry.learn.core.common.exception.MRYException;
import io.github.yeahfo.mry.learn.core.common.properties.CommonProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.TOO_MANY_REQUEST;
import static java.time.Instant.now;
import static java.util.concurrent.TimeUnit.SECONDS;

@Component
public class RedisRateLimiter implements MRYRateLimiter {
    private final StringRedisTemplate redisTemplate;
    private final CommonProperties commonProperties;

    public RedisRateLimiter( StringRedisTemplate redisTemplate, CommonProperties commonProperties ) {
        this.redisTemplate = redisTemplate;
        this.commonProperties = commonProperties;
    }

    @Override
    public void applyFor( String tenantId, String key, int tps ) {
        Optional.ofNullable( tenantId )
                .filter( StringUtils::isNotBlank )
                .orElseThrow( ( ) -> new IllegalArgumentException( "Tenant ID is required" ) );
        Optional.ofNullable( key )
                .filter( StringUtils::isNotBlank )
                .orElseThrow( ( ) -> new IllegalArgumentException( "Key is required" ) );
        apply( STR."\{key}:\{tenantId}:\{now( ).toEpochMilli( ) / 5}", tps * 5,
                5, SECONDS );
    }

    @Override
    public void applyFor( String key, int tps ) {
        //以5秒为周期统计
        apply( STR."\{key}:\{now( ).getEpochSecond( ) / 5}",
                tps * 5,
                5,
                SECONDS );
    }

    @SuppressWarnings( "SameParameterValue" )
    private void apply( String key, int limit, int expire, TimeUnit expireUnit ) {
        if ( !commonProperties.limitRate( ) ) {
            return;
        }
        if ( limit < 1 ) {
            throw new IllegalArgumentException( "Limit must be greater than or equal 1." );
        }

        String finalKey = STR."RateLimiter:\{key}";
        String count = redisTemplate.opsForValue( ).get( finalKey );
        if ( StringUtils.isNotBlank( count ) && Integer.parseInt( count ) >= limit ) {
            throw new MRYException( TOO_MANY_REQUEST, "Limit exceeded.", Map.of( "key", finalKey ) );
        }
        redisTemplate.execute( new SessionCallback<>( ) {
            @Override
            public < K, V > List< Object > execute( @NonNull RedisOperations< K, V > operations ) {
                final StringRedisTemplate redisTemplate = ( StringRedisTemplate ) operations;
                final ValueOperations< String, String > valueOperations = redisTemplate.opsForValue( );
                operations.multi( );
                valueOperations.increment( finalKey );
                redisTemplate.expire( finalKey, expire, expireUnit );
                return operations.exec( );
            }
        } );
    }
}
