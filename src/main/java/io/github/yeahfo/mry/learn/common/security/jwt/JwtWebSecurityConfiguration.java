package io.github.yeahfo.mry.learn.common.security.jwt;

import io.github.yeahfo.mry.learn.common.security.IPCookieUpdater;
import io.github.yeahfo.mry.learn.common.security.MDCFilter;
import io.github.yeahfo.mry.learn.common.tracing.TracingService;
import io.github.yeahfo.mry.learn.core.common.utils.CustomizedObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.POST;

@Configuration
public class JwtWebSecurityConfiguration {

    private final JwtService jwtService;
    private final TracingService tracingService;
    private final IPCookieUpdater ipCookieUpdater;
    private final JwtCookieFactory jwtCookieFactory;
    private final CustomizedObjectMapper objectMapper;
    private final AccessDeniedHandler accessDeniedHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    public JwtWebSecurityConfiguration( JwtService jwtService,
                                        TracingService tracingService,
                                        IPCookieUpdater ipCookieUpdater,
                                        JwtCookieFactory jwtCookieFactory,
                                        CustomizedObjectMapper objectMapper,
                                        AccessDeniedHandler accessDeniedHandler,
                                        AuthenticationEntryPoint authenticationEntryPoint,
                                        JwtAuthenticationProvider jwtAuthenticationProvider ) {
        this.jwtService = jwtService;
        this.tracingService = tracingService;
        this.ipCookieUpdater = ipCookieUpdater;
        this.jwtCookieFactory = jwtCookieFactory;
        this.objectMapper = objectMapper;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain( HttpSecurity http ) throws Exception {
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
                        .requestMatchers(
                                "/swagger-ui/*",
                                "/v3/api-docs",
                                "/v3/api-docs.yaml",
                                "/v3/api-docs/*",
                                "/about",
                                "/favicon.ico",
                                "/error",
                                "/MP_verify_qXC2acLZ7a7qm3Xp.txt",
                                "/local-manual-test/orders/**",
                                "/local-manual-test/receive-webhook",
                                "/api-testing/webhook",
                                "/api-testing/orders/**",
                                "/apptemplates/**" ).permitAll( )
                        .requestMatchers( GET, "/plans" ).permitAll( )
                        .requestMatchers( GET, "/printing-products" ).permitAll( )
                        .requestMatchers( GET, "/mobile-wx/auth2-callback" ).permitAll( )
                        .requestMatchers( GET, "/pc-wx/auth2-callback" ).permitAll( )
                        .requestMatchers( GET, "/wx/mobile-info" ).permitAll( )
                        .requestMatchers( GET, "/wx/pc-info" ).permitAll( )
                        .requestMatchers( POST, "/wx/jssdk-config" ).permitAll( )
                        .requestMatchers( POST, "/preorders/pay-callback/wxpay" ).permitAll( )
                        .requestMatchers( POST, "/verification-codes/for-register" ).permitAll( )
                        .requestMatchers( POST, "/verification-codes/for-login" ).permitAll( )
                        .requestMatchers( POST, "/verification-codes/for-find-back-password" ).permitAll( )
                        .requestMatchers( POST, "/login" ).permitAll( )
                        .requestMatchers( POST, "/verification-code-login" ).permitAll( )
                        .requestMatchers( POST, "/members/find-back-password" ).permitAll( )
                        .requestMatchers( POST, "/registrations" ).permitAll( )
                        .requestMatchers( GET, "/tenants/public-profile/*" ).permitAll( )
                        .anyRequest( ).authenticated( ) )
                .authenticationManager( authenticationManager )
                .exceptionHandling( customizer -> customizer
                        .accessDeniedHandler( accessDeniedHandler )
                        .authenticationEntryPoint( authenticationEntryPoint ) )
                .addFilterAfter( new JwtAuthenticationFilter( tracingService, objectMapper, authenticationManager ),
                        BasicAuthenticationFilter.class )
                .addFilterAfter( new JwtAutoRefreshFilter( jwtService, jwtCookieFactory, ipCookieUpdater, jwtService.getAheadAutoRefresh( ) ),
                        AuthorizationFilter.class )
                .addFilterBefore( new MDCFilter( ), ExceptionTranslationFilter.class )
                .httpBasic( AbstractHttpConfigurer::disable )
                .cors( AbstractHttpConfigurer::disable )
                .anonymous( customizer -> customizer.authenticationFilter( new JwtAnonymousAuthenticationFilter( ) ) )
                .csrf( AbstractHttpConfigurer::disable )
                .servletApi( AbstractHttpConfigurer::disable )
                .logout( AbstractHttpConfigurer::disable )
                .sessionManagement( AbstractHttpConfigurer::disable )
                .securityContext( AbstractHttpConfigurer::disable )
                .requestCache( AbstractHttpConfigurer::disable )
                .formLogin( AbstractHttpConfigurer::disable );
        return http.build( );
    }
}
