package io.github.yeahfo.mry.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

@SpringBootApplication( exclude = {
        RedisAutoConfiguration.class
} )
public class MRYLearnApplication {

    public static void main( String[] args ) {
        SpringApplication.run( MRYLearnApplication.class, args );
    }

}
