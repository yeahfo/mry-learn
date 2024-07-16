package io.github.yeahfo.mry.learn.common.security.jwt;

import io.github.yeahfo.mry.learn.common.security.CustomizedAuthenticationToken;
import io.github.yeahfo.mry.learn.common.security.IPCookieUpdater;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

public class JwtAutoRefreshFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final JwtCookieFactory jwtCookieFactory;
    private final IPCookieUpdater ipJwtCookieUpdater;
    private final int aheadAutoRefreshMilli;

    public JwtAutoRefreshFilter( JwtService jwtService,
                                 JwtCookieFactory jwtCookieFactory,
                                 IPCookieUpdater ipJwtCookieUpdater,
                                 int aheadAutoRefresh ) {
        this.jwtService = jwtService;
        this.ipJwtCookieUpdater = ipJwtCookieUpdater;
        this.jwtCookieFactory = jwtCookieFactory;
        this.aheadAutoRefreshMilli = aheadAutoRefresh * 60 * 1000;
    }

    @Override
    protected void doFilterInternal( @NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain )
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext( ).getAuthentication( );
        if ( authentication instanceof CustomizedAuthenticationToken token && authentication.isAuthenticated( ) ) {
            User user = token.getUser( );
            if ( user.isHumanUser( ) ) {
                long timeLeft = token.getExpiration( ) - Instant.now( ).toEpochMilli( );
                if ( timeLeft > 0 && timeLeft < aheadAutoRefreshMilli ) {
                    Cookie cookie = jwtCookieFactory.newJwtCookie( jwtService.generateJwt( user.memberId( ) ) );
                    response.addCookie( ipJwtCookieUpdater.updateCookie( cookie, request ) );
                }
            }
        }

        filterChain.doFilter( request, response );
    }
}
