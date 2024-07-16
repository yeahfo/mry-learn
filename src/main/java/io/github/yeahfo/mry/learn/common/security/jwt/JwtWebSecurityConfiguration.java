package io.github.yeahfo.mry.learn.common.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yeahfo.mry.learn.core.member.domain.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.POST;

@Configuration
public class JwtWebSecurityConfiguration {

    @Bean
    SecurityFilterChain springSecurityFilterChain( HttpSecurity http,
                                                   JwtAuthenticationProvider jwtAuthenticationProvider ) throws Exception {
        ProviderManager authenticationManager = new ProviderManager( jwtAuthenticationProvider );
        http
                .authorizeHttpRequests( registry -> registry
                        .requestMatchers( DELETE, "/logout" ).permitAll( )
                        .requestMatchers( POST, "/aliyun/oss-token-requisitions" ).permitAll( )
                        .requestMatchers( GET, "/qrs/submission-qrs/*" ).permitAll( )
                        .requestMatchers( GET, "/presentations/**" ).permitAll( )
                        .requestMatchers( POST, "/submissions" ).permitAll( )
                        .requestMatchers( POST, "/submissions/auto-calculate/number-input" ).permitAll( )
                        .requestMatchers( POST, "/submissions/auto-calculate/item-status" ).permitAll( )
                        .anyRequest( ).authenticated( ) )
                .authenticationManager( authenticationManager )
                .exceptionHandling( customizer -> customizer
                        .accessDeniedHandler( accessDeniedHandler )
                        .authenticationEntryPoint( authenticationEntryPoint ) )
                .and( )
                .addFilterAfter( new JwtAuthenticationFilter( authenticationManager, objectMapper, mryTracingService ), BasicAuthenticationFilter.class )
                .addFilterAfter( new AutoRefreshJwtFilter( jwtService,
                                jwtCookieFactory,
                                ipJwtCookieUpdater,
                                jwtProperties.getAheadAutoRefresh( ) ),
                        AuthorizationFilter.class )
                .addFilterBefore( new MdcFilter( ), ExceptionTranslationFilter.class )
                .httpBasic( ).disable( )
                .headers( ).and( )
                .cors( ).disable( )
                .anonymous( ).authenticationFilter( new JwtAnonymousAuthenticationFilter( ) ).and( )
                .csrf( ).disable( )
                .servletApi( ).disable( )
                .logout( ).disable( )
                .sessionManagement( ).disable( )
                .securityContext( ).disable( )
                .requestCache( ).disable( )
                .formLogin( ).disable( );
        ;
        return http.build( );
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler( ) {
        return new AccessDeniedHandler( ) {
            @Override
            public void handle( HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException ) throws IOException, ServletException {

            }
        };
    }


    @Bean
    JwtAuthenticationProvider jwtAuthenticationProvider( JwtService jwtService ) {
        return new JwtAuthenticationProvider( jwtService );
    }

    @Bean
    JwtService jwtService( MemberRepository memberRepository ) {
        return new JwtService( memberRepository );
    }
}
