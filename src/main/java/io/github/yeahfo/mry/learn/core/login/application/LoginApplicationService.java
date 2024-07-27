package io.github.yeahfo.mry.learn.core.login.application;

import io.github.yeahfo.mry.learn.common.ratelimit.RateLimiter;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.github.yeahfo.mry.learn.core.login.domain.LoginDomainService;
import io.github.yeahfo.mry.learn.core.login.domain.WxJwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static io.github.yeahfo.mry.learn.core.common.exception.MryException.authenticationException;
import static io.github.yeahfo.mry.learn.core.common.utils.CommonUtils.maskMobileOrEmail;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginApplicationService {
    private final RateLimiter rateLimiter;
    private final WxJwtService wxJwtService;
    private final LoginDomainService domainService;

    @Transactional
    public String loginWithMobileOrEmail( MobileOrEmailLoginCommand command ) {
        String mobileOrEmail = command.mobileOrEmail( );
        rateLimiter.applyFor( "Login:MobileOrEmail:" + mobileOrEmail, 1 );
        try {
            String token = domainService.loginWithMobileOrEmail(
                    mobileOrEmail,
                    command.password( ),
                    wxJwtService.wxIdInfoFromJwt( command.wxIdInfo( ) ) );
            log.info( "User[{}] logged in using password.", maskMobileOrEmail( command.mobileOrEmail( ) ) );
            return token;
        } catch ( Throwable t ) {
            //401或409时直接抛出异常
            if ( t instanceof MryException mryException &&
                 ( mryException.getCode( ).getStatus( ) == 401 || mryException.getCode( ).getStatus( ) == 409 ) ) {
                log.warn( "Password login failed for [{}].", maskMobileOrEmail( mobileOrEmail ) );
                throw mryException;
            }

            //其他情况直接一个笼统的异常
            log.warn( "Password login failed for [{}].", maskMobileOrEmail( mobileOrEmail ), t );
            throw authenticationException( );
        }
    }
}
