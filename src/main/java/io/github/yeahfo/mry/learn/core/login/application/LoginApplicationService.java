package io.github.yeahfo.mry.learn.core.login.application;

import io.github.yeahfo.mry.learn.common.ratelimit.RateLimiter;
import io.github.yeahfo.mry.learn.common.security.jwt.JwtService;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.github.yeahfo.mry.learn.core.login.domain.LoginDomainService;
import io.github.yeahfo.mry.learn.core.login.domain.WxJwtService;
import io.github.yeahfo.mry.learn.core.member.domain.Member;
import io.github.yeahfo.mry.learn.core.member.domain.MemberRepository;
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
    private final JwtService jwtService;
    private final RateLimiter rateLimiter;
    private final WxJwtService wxJwtService;
    private final LoginDomainService domainService;
    private final MemberRepository memberRepository;

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

    @Transactional
    public String loginWithVerificationCode( VerificationCodeLoginCommand command ) {
        String mobileOrEmail = command.mobileOrEmail( );
        rateLimiter.applyFor( "Login:MobileOrEmail:" + mobileOrEmail, 1 );
        try {
            String token = domainService.loginWithVerificationCode(
                    mobileOrEmail,
                    command.verification( ),
                    wxJwtService.wxIdInfoFromJwt( command.wxIdInfo( ) ) );
            log.info( "User[{}] logged in using verification code.", maskMobileOrEmail( mobileOrEmail ) );
            return token;
        } catch ( Throwable t ) {
            //401或409时直接抛出异常
            if ( t instanceof MryException mryException &&
                    ( mryException.getCode( ).getStatus( ) == 401 || mryException.getCode( ).getStatus( ) == 409 ) ) {
                log.warn( "Verification code login failed for [{}].", maskMobileOrEmail( mobileOrEmail ) );
                throw mryException;
            }

            //其他情况直接一个笼统的异常
            log.warn( "Verification code login failed for [{}].", maskMobileOrEmail( mobileOrEmail ), t );
            throw authenticationException( );
        }
    }

    @Transactional
    public String refreshToken( User user ) {
        rateLimiter.applyFor( "Login:RefreshToken:All", 1000 );
        Member member = memberRepository.findById( user.memberId( ) ).orElseThrow( MryException::authenticationException );
        log.info( "User[{}] refreshed token.", user.memberId( ) );
        return jwtService.generateJwt( member.id( ) );
    }
}
