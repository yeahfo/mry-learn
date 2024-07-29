package io.github.yeahfo.mry.learn.common.security.jwt;

import io.github.yeahfo.mry.learn.common.security.CustomizedAuthenticationToken;
import io.github.yeahfo.mry.learn.core.member.domain.Member;
import io.github.yeahfo.mry.learn.core.member.domain.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static io.github.yeahfo.mry.learn.core.common.domain.User.humanUser;

@Getter
@Setter
@Validated
@Component
public class JwtService {
    @Value( "${spring.security.oauth2.authorizationserver.customizer.jwt.issuer:Authorities}" )
    private String issuer;
    @Value( "${spring.security.oauth2.authorizationserver.customizer.jwt.secret:11111111111111111111111111111111}" )
    private String secret;

    @Min( value = 60 )
    @Max( value = 43200 )
    @Value( "${spring.security.oauth2.authorizationserver.customizer.jwt.expire:#{60}}" )//30天
    private int expire;//分钟

    @Min( value = 1 )
    @Max( value = 2880 )
    @Value( "${spring.security.oauth2.authorizationserver.customizer.jwt.ahead-auto-refresh:#{1}}" )//2天
    private int aheadAutoRefresh;//分钟
    private final MemberRepository memberRepository;

    public JwtService( MemberRepository memberRepository ) {
        this.memberRepository = memberRepository;
    }

    public String generateJwt( String memberId ) {
        Date now = new Date( );
        Date expirationDate = new Date( now.getTime( ) + expire * 60L * 1000L );
        return generateJwt( memberId, expirationDate );
    }

    public String generateJwt( String memberId, Date expirationDate ) {
        Claims claims = Jwts.claims( ).subject( memberId ).build( );

        return Jwts.builder( )
                .claims( claims )
                .issuer( issuer )
                .issuedAt( new Date( ) )
                .expiration( expirationDate )
                .signWith( Keys.hmacShaKeyFor( secret.getBytes( StandardCharsets.UTF_8 ) ), Jwts.SIG.HS256 )
                .compact( );
    }

    public CustomizedAuthenticationToken tokenFrom( String jwt ) {
        Claims claims = Jwts.parser( ).verifyWith( Keys.hmacShaKeyFor( secret.getBytes( StandardCharsets.UTF_8 ) ) ).build( )
                .parseSignedClaims( jwt ).getPayload( );
        String memberId = claims.getSubject( );
        Member member = memberRepository.findById( memberId ).orElseThrow( );
        member.checkActive( );
        long expiration = claims.getExpiration( ).getTime( );
        return new CustomizedAuthenticationToken( humanUser( memberId, member.name( ), member.tenantId( ), member.role( ) ), expiration );
    }

}
