package io.github.yeahfo.mry.learn.core.login;

import io.github.yeahfo.mry.learn.common.security.IPCookieUpdater;
import io.github.yeahfo.mry.learn.common.security.jwt.JwtCookieFactory;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.login.application.JwtTokenRepresentation;
import io.github.yeahfo.mry.learn.core.login.application.LoginApplicationService;
import io.github.yeahfo.mry.learn.core.login.application.MobileOrEmailLoginCommand;
import io.github.yeahfo.mry.learn.core.login.application.VerificationCodeLoginCommand;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static io.github.yeahfo.mry.learn.common.spring.SpringCommonConfiguration.AUTHORIZATION_BEARER_JWT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Tag( name = "Login", description = "Common login APIs" )
public class LoginResource {
    private final IPCookieUpdater ipCookieUpdater;
    private final JwtCookieFactory jwtCookieFactory;
    private final LoginApplicationService applicationService;


    @PostMapping( value = "/login", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< JwtTokenRepresentation > login( @RequestBody @Valid MobileOrEmailLoginCommand command,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response ) {
        String jwt = applicationService.loginWithMobileOrEmail( command );
        response.addCookie( ipCookieUpdater.updateCookie( jwtCookieFactory.newJwtCookie( jwt ), request ) );
        return ResponseEntity.ok( JwtTokenRepresentation.builder( ).token( jwt ).build( ) );
    }

    @PostMapping( value = "/verification-code-login", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< JwtTokenRepresentation > loginWithVerificationCode( HttpServletRequest request,
                                                                               HttpServletResponse response,
                                                                               @RequestBody @Valid VerificationCodeLoginCommand command ) {
        String jwt = applicationService.loginWithVerificationCode( command );
        response.addCookie( ipCookieUpdater.updateCookie( jwtCookieFactory.newJwtCookie( jwt ), request ) );
        return ResponseEntity.ok( JwtTokenRepresentation.builder( ).token( jwt ).build( ) );
    }


    @DeleteMapping( value = "/logout" )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    public ResponseEntity< Void > logout( HttpServletRequest request,
                                          HttpServletResponse response,
                                          @AuthenticationPrincipal User user ) {
        response.addCookie( ipCookieUpdater.updateCookie( jwtCookieFactory.logoutCookie( ), request ) );
        if ( user.isLoggedIn( ) ) {
            log.info( "User[{}] tried log out.", user.memberId( ) );
        }
        return ResponseEntity.noContent( ).build( );
    }

    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @PutMapping( value = "/refresh-token", produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< JwtTokenRepresentation > refreshToken( HttpServletRequest request,
                                                                  HttpServletResponse response,
                                                                  @AuthenticationPrincipal User user ) {
        String jwt = applicationService.refreshToken( user );
        response.addCookie( ipCookieUpdater.updateCookie( jwtCookieFactory.newJwtCookie( jwt ), request ) );
        return ResponseEntity.ok( JwtTokenRepresentation.builder( ).token( jwt ).build( ) );
    }
}
