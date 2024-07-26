package io.github.yeahfo.mry.learn.common.spring;

import io.github.yeahfo.mry.learn.core.common.utils.CustomizedObjectMapper;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springdoc.core.customizers.OpenApiCustomizer;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@EnableAsync
@EnableRetry
@EnableCaching
@Configuration
public class SpringCommonConfiguration {

    @Bean
    CustomizedObjectMapper objectMapper( ) {
        return new CustomizedObjectMapper( );
    }

    public static final String AUTHORIZATION_BEARER_JWT = "AuthorizationBearerJWT";

    @Bean
    @Profile( { "dev", "debug" } )
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
//                        .addSchemas( TOKEN_SCHEMA.getName( ), TOKEN_SCHEMA )
//                        .addSchemas( ROLE_SCHEMA.getName( ), ROLE_SCHEMA )
                );
    }

    @Bean
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
