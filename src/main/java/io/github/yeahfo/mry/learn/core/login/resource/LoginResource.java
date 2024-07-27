package io.github.yeahfo.mry.learn.core.login.resource;

import io.github.yeahfo.mry.learn.common.security.IPCookieUpdater;
import io.github.yeahfo.mry.learn.common.security.jwt.JwtCookieFactory;
import io.github.yeahfo.mry.learn.core.login.application.LoginApplicationService;
import io.github.yeahfo.mry.learn.core.login.application.MobileOrEmailLoginCommand;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class LoginResource {
    private final IPCookieUpdater ipCookieUpdater;
    private final JwtCookieFactory jwtCookieFactory;
    private final LoginApplicationService applicationService;

    @PostMapping( "/login" )
    public ResponseEntity< JwtTokenRepresentation > login( @RequestBody @Valid MobileOrEmailLoginCommand command,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response ) {
        String jwt = applicationService.loginWithMobileOrEmail( command );
        response.addCookie( ipCookieUpdater.updateCookie( jwtCookieFactory.newJwtCookie( jwt ), request ) );
        return ResponseEntity.ok( JwtTokenRepresentation.builder( ).token( jwt ).build( ) );
    }
}
