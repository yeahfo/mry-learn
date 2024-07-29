package io.github.yeahfo.mry.learn.core.login.domain;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@RequiredArgsConstructor
public class WxJwtService {
    private static final String MOBILE_OPEN_ID = "mo";
    private static final String PC_OPEN_ID = "po";
    @Value( "${spring.security.oauth2.authorizationserver.customizer.jwt.issuer:Authorities}" )
    private String issuer;
    @Value( "${spring.security.oauth2.authorizationserver.customizer.jwt.secret:1111111111111111111111111111111111111111111111111111111111111111}" )
    private String secret;

    public WxIdInfo wxIdInfoFromJwt( String jwt ) {
        if ( isBlank( jwt ) ) {
            return null;
        }
        try {
            Claims claims = Jwts.parser( ).verifyWith( Keys.hmacShaKeyFor( secret.getBytes( StandardCharsets.UTF_8 ) ) ).build( )
                    .parseEncryptedClaims( jwt ).getPayload( );
            String wxUnionId = claims.getSubject( );
            String mobileWxOpenId = claims.get( MOBILE_OPEN_ID, String.class );
            String pcWxOpenId = claims.get( PC_OPEN_ID, String.class );
            return WxIdInfo.builder( )
                    .wxUnionId( wxUnionId )
                    .mobileWxOpenId( mobileWxOpenId )
                    .pcWxOpenId( pcWxOpenId )
                    .build( );
        } catch ( Throwable t ) {
            return null;
        }
    }

    public String generateMobileWxIdInfoJwt( String wxUnionId, String mobileWxOpenId ) {
        return generateWxIdInfoJwt( wxUnionId, mobileWxOpenId, null );
    }

    public String generatePcWxIdInfoJwt( String unionId, String pcWxOpenId ) {
        return generateWxIdInfoJwt( unionId, null, pcWxOpenId );
    }

    private String generateWxIdInfoJwt( String unionId, String mobileWxOpenId, String pcWxOpenId ) {
        Claims claims = Jwts.claims( ).subject( unionId ).build( );

        if ( isNotBlank( mobileWxOpenId ) ) {
            claims.put( MOBILE_OPEN_ID, mobileWxOpenId );
        }

        if ( isNotBlank( pcWxOpenId ) ) {
            claims.put( PC_OPEN_ID, pcWxOpenId );
        }

        Date now = new Date( );
        Date expirationDate = new Date( now.getTime( ) + 30 * 60L * 1000L );

        return Jwts.builder( )
                .claims( claims )
                .issuer( issuer )
                .issuedAt( now )
                .expiration( expirationDate )
                .signWith( Keys.hmacShaKeyFor( secret.getBytes( StandardCharsets.UTF_8 ) ), Jwts.SIG.HS512 )
                .compact( );
    }
}
