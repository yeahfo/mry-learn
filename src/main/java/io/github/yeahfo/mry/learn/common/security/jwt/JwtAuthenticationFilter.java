package io.github.yeahfo.mry.learn.common.security.jwt;

import io.github.yeahfo.mry.learn.common.tracing.TracingService;
import io.github.yeahfo.mry.learn.core.common.exception.Error;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.github.yeahfo.mry.learn.core.common.utils.CustomizedObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.AUTH_COOKIE_NAME;
import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.BEARER;
import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.web.util.WebUtils.getCookie;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TracingService tracingService;
    private final CustomizedObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter( TracingService tracingService,
                                    CustomizedObjectMapper objectMapper,
                                    AuthenticationManager authenticationManager ) {
        this.tracingService = tracingService;
        this.objectMapper = objectMapper;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal( @NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain ) throws ServletException, IOException {
        try {
            Authentication token = convert( request );
            if ( token == null ) {
                filterChain.doFilter( request, response );
                return;
            }
            if ( authenticationIsRequired( ) ) {
                Authentication authenticatedToken = authenticationManager.authenticate( token );
                SecurityContextHolder.getContext( ).setAuthentication( authenticatedToken );
            }
        } catch ( MryException ex ) {
            SecurityContextHolder.clearContext( );

            int status = ex.getCode( ).getStatus( );
            if ( status == 401 || status == 409 ) {//对于401和409异常，直接中断执行并返回
                response.setStatus( status );
                response.setContentType( APPLICATION_JSON_VALUE );
                response.setCharacterEncoding( UTF_8 );
                String traceId = tracingService.currentTraceId( );
                Error error = new Error( ex.getCode( ),
                        status,
                        ex.getUserMessage( ),
                        request.getRequestURI( ),
                        traceId,
                        null );

                PrintWriter writer = response.getWriter( );
                writer.print( objectMapper.writeValueAsString( error.representation( ) ) );
                writer.flush( );
                return;
            }
        } catch ( Throwable t ) {//其他异常继续执行，之后的MryAuthenticationEntryPoint会捕捉到了
            logger.error( t.getMessage( ), t );
            SecurityContextHolder.clearContext( );
        }

        filterChain.doFilter( request, response );
    }

    private Authentication convert( HttpServletRequest request ) {
        //先提取cookie中的jwt，如果有，无论是否合法均不再检查authorization头
        Cookie tokenCookie = getCookie( request, AUTH_COOKIE_NAME );
        if ( tokenCookie != null && isNotBlank( tokenCookie.getValue( ) ) ) {
            return new JwtAuthenticationToken( tokenCookie.getValue( ) );
        }

        //如果没有提供cookie，再尝试检查authorization头
        String bearerToken = extractBearerToken( request );
        if ( isNotBlank( bearerToken ) ) {
            return new JwtAuthenticationToken( bearerToken );
        }
        return null;
    }


    private String extractBearerToken( HttpServletRequest request ) {
        String authorizationString = request.getHeader( AUTHORIZATION );

        if ( isBlank( authorizationString ) || !authorizationString.startsWith( BEARER ) ) {
            return null;
        }

        return authorizationString.substring( BEARER.length( ) );
    }


    private boolean authenticationIsRequired( ) {
        Authentication existingAuth = SecurityContextHolder.getContext( ).getAuthentication( );
        if ( existingAuth == null || !existingAuth.isAuthenticated( ) ) {
            return true;
        }

        return existingAuth instanceof AnonymousAuthenticationToken;
    }

}
