package io.eventuate.messaging.redis.spring.consumer;

import io.eventuate.coordination.leadership.LeaderSelectorFactory;
import io.eventuate.messaging.partitionmanagement.*;
import io.eventuate.messaging.redis.spring.common.EventuateMessagingRedissonClientsConfiguration;
import io.eventuate.messaging.redis.spring.common.RedissonClients;
import io.eventuate.messaging.redis.spring.leadership.RedisLeaderSelector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

// Override to reuse Spring Redis or Redisson starter, supports passwords.
@Configuration
@Import( EventuateMessagingRedissonClientsConfiguration.class )
public class MessageConsumerRedisConfiguration {

    @Bean
    public MessageConsumerRedisImpl messageConsumerRedis( RedisTemplate< String, String > redisTemplate,
                                                          CoordinatorFactory coordinatorFactory ) {

            return new MessageConsumerRedisImpl( redisTemplate,
                    coordinatorFactory,
                    500,
                    10000 );
    }

    @Bean
    public CoordinatorFactory redisCoordinatorFactory( AssignmentManager assignmentManager,
                                                       AssignmentListenerFactory assignmentListenerFactory,
                                                       MemberGroupManagerFactory memberGroupManagerFactory,
                                                       LeaderSelectorFactory leaderSelectorFactory,
                                                       GroupMemberFactory groupMemberFactory ) {
        return new CoordinatorFactoryImpl( assignmentManager,
                assignmentListenerFactory,
                memberGroupManagerFactory,
                leaderSelectorFactory,
                groupMemberFactory,
                2 );
    }

    @Bean
    public GroupMemberFactory groupMemberFactory( RedisTemplate< String, String > redisTemplate ) {
        return ( groupId, memberId ) ->
                new RedisGroupMember( redisTemplate,
                        groupId,
                        memberId,
                        10000 );
    }

    @Bean
    @ConditionalOnMissingBean
    public LeaderSelectorFactory leaderSelectorFactory( RedissonClients redissonClients ) {
        return ( lockId, leaderId, leaderSelectedCallback, leaderRemovedCallback ) ->
                new RedisLeaderSelector( redissonClients,
                        lockId,
                        leaderId,
                        10000,
                        leaderSelectedCallback,
                        leaderRemovedCallback );
    }

    @Bean
    public MemberGroupManagerFactory memberGroupManagerFactory( RedisTemplate< String, String > redisTemplate ) {
        return ( groupId, memberId, groupMembersUpdatedCallback ) ->
                new RedisMemberGroupManager( redisTemplate,
                        groupId,
                        memberId,
                        2000, // Default 1000ms, but on redis cluster need > 1000ms.
                        groupMembersUpdatedCallback );
    }

    @Bean
    public AssignmentListenerFactory assignmentListenerFactory( RedisTemplate< String, String > redisTemplate ) {
        return ( groupId, memberId, assignmentUpdatedCallback ) ->
                new RedisAssignmentListener( redisTemplate,
                        groupId,
                        memberId,
                        2000, // Default 1000ms, but on redis cluster need > 1000ms.
                        assignmentUpdatedCallback );
    }

    @Bean
    public AssignmentManager assignmentManager( RedisTemplate< String, String > redisTemplate ) {
        return new RedisAssignmentManager( redisTemplate, 36000000 );
    }
}
