package io.eventuate.messaging.redis.spring.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

// Override to reuse Spring Redis or Redisson starter, supports passwords.
@SuppressWarnings( "unused" )
public class ChannelProcessor {
    private final Logger logger = LoggerFactory.getLogger( getClass( ) );
    private final String subscriptionIdentificationInfo;
    private final CountDownLatch stopCountDownLatch = new CountDownLatch( 1 );
    private final AtomicBoolean running = new AtomicBoolean( false );
    private final String subscriberId;
    private final String channel;
    private final RedisMessageHandler messageHandler;
    private final RedisTemplate< String, String > redisTemplate;
    private final long timeInMillisecondsToSleepWhenKeyDoesNotExist;
    private final long blockStreamTimeInMilliseconds;

    public ChannelProcessor( RedisTemplate< String, String > redisTemplate,
                             String subscriberId,
                             String channel,
                             RedisMessageHandler messageHandler,
                             String subscriptionIdentificationInfo,
                             long timeInMillisecondsToSleepWhenKeyDoesNotExist,
                             long blockStreamTimeInMilliseconds ) {

        this.redisTemplate = redisTemplate;
        this.subscriberId = subscriberId;
        this.channel = channel;
        this.messageHandler = messageHandler;
        this.subscriptionIdentificationInfo = subscriptionIdentificationInfo;
        this.timeInMillisecondsToSleepWhenKeyDoesNotExist = timeInMillisecondsToSleepWhenKeyDoesNotExist;
        this.blockStreamTimeInMilliseconds = blockStreamTimeInMilliseconds;

        logger.info( "Channel processor is created (channel = {}, {})", channel, subscriptionIdentificationInfo );
    }


    public void process( ) {
        try {
            logger.info( "Channel processor started processing (channel = {}, {})", channel, subscriptionIdentificationInfo );
            running.set( true );
            makeSureConsumerGroupExists( );
            processPendingRecords( );
            processRegularRecords( );
            stopCountDownLatch.countDown( );
            logger.info( "Channel processor finished processing (channel = {}, {})", channel, subscriptionIdentificationInfo );
        } catch ( Throwable e ) {
            logger.error( "Channel processor got error: (channel = {}, subscriberId = {})", channel, subscriberId );
            logger.error( "Channel processor got error", e );
            throw e;
        }
    }

    public void stop( ) {
        logger.info( "stopping channel (channel = {}, {})", channel, subscriptionIdentificationInfo );
        running.set( false );
        try {
            stopCountDownLatch.await( );
            logger.info( "Stopped channel (channel = {}, {})", channel, subscriptionIdentificationInfo );
        } catch ( InterruptedException e ) {
            logger.error( "Stopping channel failed (channel = {}, {})", channel, subscriptionIdentificationInfo );
            logger.error( "Stopping channel failed", e );
            throw new RuntimeException( e );
        }
    }

    private void makeSureConsumerGroupExists( ) {
        logger.info( "Ensuring consumer group exists {} {}", channel, subscriberId );
        while ( running.get( ) ) {
            try {
                logger.info( "Creating group {} {}", channel, subscriberId );
                redisTemplate.opsForStream( ).createGroup( channel, ReadOffset.from( "0" ), subscriberId );
                logChannel();
                return;
            } catch ( InvalidDataAccessApiUsageException e ) {
                if ( isKeyDoesNotExist( e ) ) {
                    logger.info( "Stream {} does not exist!", channel );
                    sleep( timeInMillisecondsToSleepWhenKeyDoesNotExist );
                    continue;
                } else if ( isGroupExistsAlready( e ) ) {
                   logChannel();
                    return;
                }
                //noinspection StringConcatenationArgumentToLogCall
                logger.error( "Got exception ensuring consumer group exists: " + channel, e );
                throw e;
            }
        }
    }
    private void logChannel() {
        logger.info( "Ensuring consumer group exists {}", channel );
    }

    private boolean isKeyDoesNotExist( InvalidDataAccessApiUsageException e ) {
        return isRedisCommandExceptionContainingMessage( e, "ERR The XGROUP subcommand requires the key to exist" );
    }

    private boolean isGroupExistsAlready( InvalidDataAccessApiUsageException e ) {
        return isRedisCommandExceptionContainingMessage( e, "Consumer Group name already exists" );
    }

    private boolean isRedisCommandExceptionContainingMessage( InvalidDataAccessApiUsageException e, String expectedMessage ) {
        String message = e.getCause( ).getMessage( );

        return message != null &&
               message.contains( expectedMessage );
    }

    private void processPendingRecords( ) {
        logger.info( "Processing pending records {}", channel );
        while ( running.get( ) ) {
            List< MapRecord< String, Object, Object > > pendingRecords = getPendingRecords( );

            if ( pendingRecords.isEmpty( ) ) {
                return;
            }

            processRecords( pendingRecords );
        }
        logger.info( "Processing pending records finished {}", channel );
    }

    private void processRegularRecords( ) {
        logger.trace( "Processing regular records {}", channel );
        while ( running.get( ) ) {
            processRecords( getUnprocessedRecords( ) );
        }
        logger.trace( "Processing regular records finished {}", channel );
    }

    private void processRecords( List< MapRecord< String, Object, Object > > records ) {
        records.forEach( entries ->
                entries
                        .getValue( )
                        .values( )
                        .forEach( v -> processMessage( v.toString( ), entries.getId( ) ) ) );
    }

    private void processMessage( String message, RecordId recordId ) {
        logger.trace( "Channel processor {} with channel {} got message: {}", subscriptionIdentificationInfo, channel, message );

        try {
            logger.trace( "Invoked message handler" );
            messageHandler.accept( new RedisMessage( message ) );
            logger.trace( "Message handler invoked" );
        } catch ( Throwable t ) {
            logger.error( "Message processing failed", t );
            stopCountDownLatch.countDown( );
            throw t;
        }

        redisTemplate.opsForStream( ).acknowledge( channel, subscriberId, recordId );
    }

    private List< MapRecord< String, Object, Object > > getPendingRecords( ) {
        return getRecords( ReadOffset.from( "0" ), StreamReadOptions.empty( ) );
    }

    private List< MapRecord< String, Object, Object > > getUnprocessedRecords( ) {
        return getRecords( ReadOffset.from( ">" ), StreamReadOptions.empty( ).block( Duration.ofMillis( blockStreamTimeInMilliseconds ) ) );
    }

    private List< MapRecord< String, Object, Object > > getRecords( ReadOffset readOffset, StreamReadOptions options ) {
        //noinspection unchecked
        List< MapRecord< String, Object, Object > > records = redisTemplate
                .opsForStream( )
                .read( Consumer.from( subscriberId, subscriberId ), options, StreamOffset.create( channel, readOffset ) );

        if ( !Objects.requireNonNull( records ).isEmpty( ) ) {
            logger.trace( "getRecords {} {} found {} records", channel, readOffset, records.size( ) );
        }

        return records;
    }

    private void sleep( long timeoutInMilliseconds ) {
        try {
            Thread.sleep( timeoutInMilliseconds );
        } catch ( Exception e ) {
            logger.error( "Sleeping failed", e );
            throw new RuntimeException( e );
        }
    }
}
