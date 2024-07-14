package io.github.yeahfo.mry.learn.core.member.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class ApplicationConfiguration {

    @Bean
    MongoTransactionManager mongoTransactionManager( MongoDatabaseFactory mongoDatabaseFactory ) {
        return new MongoTransactionManager( mongoDatabaseFactory );
    }
}
