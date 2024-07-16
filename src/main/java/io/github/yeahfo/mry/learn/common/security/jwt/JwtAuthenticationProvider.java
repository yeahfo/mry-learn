package io.github.yeahfo.mry.learn.common.security.jwt;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtService jwtService;

    public JwtAuthenticationProvider( JwtService jwtService ) {
        this.jwtService = jwtService;
    }

    @Override
    public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
        JwtAuthenticationToken jwtAuthenticationToken = ( JwtAuthenticationToken ) authentication;
        return jwtService.tokenFrom( jwtAuthenticationToken.getJwt( ) );
    }

    @Override
    public boolean supports( Class< ? > authentication ) {
        return JwtAuthenticationToken.class.isAssignableFrom( authentication );
    }
}
