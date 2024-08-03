package io.github.yeahfo.mry.learn.common.spring;

import io.github.yeahfo.mry.learn.core.common.utils.CustomizedObjectMapper;
import io.github.yeahfo.mry.learn.core.login.application.JwtTokenRepresentation;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@EnableAsync
@EnableRetry
@EnableCaching
@Configuration
@EnableTransactionManagement
public class SpringCommonConfiguration {

    @Bean
    MongoTransactionManager mongoTransactionManager( MongoDatabaseFactory mongoDatabaseFactory ) {
        return new MongoTransactionManager( mongoDatabaseFactory );
    }
    @Bean
    CustomizedObjectMapper objectMapper( ) {
        return new CustomizedObjectMapper( );
    }

    public static final String AUTHORIZATION_BEARER_JWT = "AuthorizationBearerJWT";
    public static final String STRING = String.class.getSimpleName( ).toLowerCase( );
    public static final Schema< ? > TOKEN_SCHEMA = new Schema<>( )
            .required( List.of( "access_token", "expires_in", "refresh_token", "refresh_expires_in", "token_type" ) )
            .name( JwtTokenRepresentation.class.getSimpleName( ) )
            .title( "AccessTokenRepresentation" )
            .description( "token信息" )
            .addProperty( "access_token", new Schema<>( ).description( "访问token值" ).type( STRING ) );

    //            .addProperty( "access_token", new Schema<>( ).description( "访问token值" ).type( STRING ) )
//            .addProperty( "expires_in", new Schema<>( ).description( "访问token过期时间(秒)" ).type( INTEGER ) )
//            .addProperty( "refresh_token", new Schema<>( ).description( "刷新token值" ).type( STRING ) )
//            .addProperty( "refresh_expires_in", new Schema<>( ).description( "刷新token过期时间(秒)" ).type( INTEGER ) )
//            .addProperty( "token_type", new Schema<>( ).description( "token类型, 固定值`Bearer`" ).type( STRING ) )
//            .addProperty( "id_token", new Schema<>( ).description( "身份token值" ).type( STRING ) )
//            .addProperty( "not-before-policy", new Schema<>( ).description( "策略" ).type( INTEGER ) )
//            .addProperty( "session_state", new Schema<>( ).description( "会话状态值" ).type( STRING ) )
//            .addProperty( "error", new Schema<>( ).description( "请求出错的错误码" ).type( STRING ) )
//            .addProperty( "error_description", new Schema<>( ).description( "请求出错的错误详情" ).type( STRING ) )
//            .addProperty( "error_uri", new Schema<>( ).description( "请求出错的错误遵循规范发行地址" ).type( STRING ) );
    @Bean
    @ConditionalOnProperty( name = "springdoc.api-docs.enabled", matchIfMissing = true )
    OpenAPI openAPI( ) {
        SecurityScheme JWT_SECURITY_SCHEME = new SecurityScheme( )
                .name( AUTHORIZATION_BEARER_JWT )
                .type( SecurityScheme.Type.HTTP )
                .scheme( "bearer" )
                .bearerFormat( "JWT" )
                .description( """
                        *携带 `HTTP` 请求头 `Authorization`: `Bearer` `JSON_WEB_TOKEN_VALUE`*
                        ##### 如:
                        ```shell
                        POST /api/v1/users HTTP/1.1 \s
                        Host: api.server.com
                        Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5...
                        ```
                        ###### 在线调试在以下输入框直接粘贴Token值 (不包含 `Bearer `前缀)
                        """ );
        //noinspection HttpUrlsUsage
        return new OpenAPI( ).info( new Info( )
                        .title( "MRY OpenAPI 文档" )
                        .version( "0.0.1" )
                        .description( "提供MRY数据服务" )
                        .contact( new Contact( ).name( "MRY" ).url( "http://www.mry.com" )
                                .email( "mry@mry.com" ) ) )
                .components( new Components( )
                                .addSecuritySchemes( JWT_SECURITY_SCHEME.getName( ), JWT_SECURITY_SCHEME )
                                .addSchemas( TOKEN_SCHEMA.getName( ), TOKEN_SCHEMA )
//                        .addSchemas( ROLE_SCHEMA.getName( ), ROLE_SCHEMA )
                );
    }

    @Bean
    @ConditionalOnProperty( name = "springdoc.api-docs.enabled", matchIfMissing = true )
    public OpenApiCustomizer sortSchemasAlphabetically( ) {
        return api -> {
            Paths paths = new Paths( );
            api.getPaths( ).entrySet( )
                    .stream( )
                    .sorted( Comparator.comparing( entry -> entry.getKey( ).length( ) ) )
                    .collect( toMap( Map.Entry::getKey, Map.Entry::getValue, ( x, y ) -> y, LinkedHashMap::new ) )
                    .forEach( paths::addPathItem );
            api.setPaths( paths );
        };
    }
}
