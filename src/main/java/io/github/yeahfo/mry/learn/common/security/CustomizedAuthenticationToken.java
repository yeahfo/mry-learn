package io.github.yeahfo.mry.learn.common.security;

import io.github.yeahfo.mry.learn.core.common.domain.User;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Getter
public class CustomizedAuthenticationToken extends AbstractAuthenticationToken {
    private final User user;
    private final long expiration;

    public CustomizedAuthenticationToken( User user, long expiration ) {
        super( List.of( new SimpleGrantedAuthority( "ROLE_" + user.role( ).name( ) ) ) );
        this.user = user;
        this.expiration = expiration;
        setAuthenticated( true );
    }


    @Override
    public Object getCredentials( ) {
        return null;
    }

    @Override
    public Object getPrincipal( ) {
        return user;
    }

    @Override
    public void eraseCredentials( ) {
        super.eraseCredentials( );
    }
}
