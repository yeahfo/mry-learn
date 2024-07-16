package io.github.yeahfo.mry.learn.common.security;

import io.github.yeahfo.mry.learn.core.common.domain.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class MDCFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal( @NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain )
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext( ).getAuthentication( );
        if ( authentication instanceof CustomizedAuthenticationToken token ) {
            User user = token.getUser( );
            MDC.put( "memberId", user.memberId( ) );
            MDC.put( "tenantId", user.tenantId( ) );
        }
        filterChain.doFilter( request, response );
        MDC.clear( );
    }
}
