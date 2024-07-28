package io.github.yeahfo.mry.learn.core.login.domain;

import io.github.yeahfo.mry.learn.common.password.PasswordEncoderFactories;
import io.github.yeahfo.mry.learn.common.security.jwt.JwtService;
import io.github.yeahfo.mry.learn.common.wx.auth.mobile.MobileWxAuthService;
import io.github.yeahfo.mry.learn.common.wx.auth.mobile.MobileWxAuthUserInfo;
import io.github.yeahfo.mry.learn.common.wx.auth.pc.PcWxAuthService;
import io.github.yeahfo.mry.learn.common.wx.auth.pc.PcWxAuthUserInfo;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.github.yeahfo.mry.learn.core.member.domain.Member;
import io.github.yeahfo.mry.learn.core.member.domain.MemberDomainService;
import io.github.yeahfo.mry.learn.core.member.domain.MemberRepository;
import io.github.yeahfo.mry.learn.core.verification.domain.VerificationCodeChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static io.github.yeahfo.mry.learn.core.common.exception.MryException.authenticationException;
import static io.github.yeahfo.mry.learn.core.verification.domain.VerificationCodeType.LOGIN;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginDomainService {
    private static final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder( );
    private final JwtService jwtService;
    private final PcWxAuthService pcWxAuthService;
    private final MemberRepository memberRepository;
    private final MemberDomainService memberDomainService;
    private final MobileWxAuthService mobileWxAuthService;
    private final VerificationCodeChecker verificationCodeChecker;

    public String loginWithMobileOrEmail( String mobileOrEmail,
                                          String password,
                                          WxIdInfo wxIdInfo ) {
        Member member = memberRepository.findByMobileOrEmail( mobileOrEmail )
                .orElseThrow( MryException::authenticationException );
        if ( !passwordEncoder.matches( password, member.password( ) ) ) {
            memberDomainService.recordMemberFailedLogin( member );
            throw authenticationException( );
        }
        member.checkActive( );
        return generateJwtAndTryBindWx( member, wxIdInfo );
    }

    private String generateJwtAndTryBindWx( Member member, WxIdInfo wxIdInfo ) {
        try {
            tryBindWx( member, wxIdInfo );
        } catch ( Throwable t ) {
            log.warn( "Failed bind wx[unionId:{},mobileWxOpenId:{},pcWxOpenId:{}] to member[{}].",
                    wxIdInfo.wxUnionId( ), wxIdInfo.mobileWxOpenId( ), wxIdInfo.pcWxOpenId( ), member.id( ), t );
        }

        return jwtService.generateJwt( member.id( ) );
    }

    public String loginWithVerificationCode( String mobileOrEmail, String verification, WxIdInfo wxIdInfo ) {
        verificationCodeChecker.check( mobileOrEmail, verification, LOGIN );
        Member member = memberRepository.findByMobileOrEmail( mobileOrEmail )
                .orElseThrow( MryException::authenticationException );
        member.checkActive( );
        return generateJwtAndTryBindWx( member, wxIdInfo );
    }

    private void tryBindWx( Member member, WxIdInfo wxIdInfo ) {
        if ( wxIdInfo == null ) {
            return;
        }

        String wxUnionId = wxIdInfo.wxUnionId( );
        String mobileWxOpenId = wxIdInfo.mobileWxOpenId( );
        String pcWxOpenId = wxIdInfo.pcWxOpenId( );

        if ( isNotBlank( mobileWxOpenId ) ) {
            //只要能够同时完成登录，并提供微信unionId的JWT信息，即可绑定，可能导致将先前的绑定踢掉
            member.bindMobileWx( wxUnionId, mobileWxOpenId, member.toUser( ) );

            //尝试获取微信昵称和头像
            mobileWxAuthService.getAccessToken( wxUnionId ).ifPresent( token -> {
                try {
                    MobileWxAuthUserInfo userInfo = mobileWxAuthService.fetchUserInfo( token, mobileWxOpenId );
                    member.updateMobileWxInfo( mobileWxOpenId, userInfo.nickname( ), userInfo.headerImageUrl( ), member.toUser( ) );
                } catch ( Throwable t ) {
                    log.warn( "Failed to update mobile wx info for member[{}], will continue bind without these info.", member.id( ), t );
                }
            } );

            log.info( "Bind mobile wx[unionId={},mobileWxOpenId={}] to member[{}].", wxUnionId, mobileWxOpenId, member.id( ) );
            memberRepository.save( member );
        } else if ( isNotBlank( pcWxOpenId ) ) {
            //只要能够同时完成登录，并提供微信unionId的JWT信息，即可绑定，可能导致将先前的绑定踢掉
            member.bindPcWx( wxUnionId, pcWxOpenId, member.toUser( ) );

            //尝试获取微信昵称和头像
            pcWxAuthService.getAccessToken( wxUnionId ).ifPresent( token -> {
                try {
                    PcWxAuthUserInfo userInfo = pcWxAuthService.fetchUserInfo( token, pcWxOpenId );
                    member.updatePcWxInfo( pcWxOpenId, userInfo.nickname( ), userInfo.headerImageUrl( ), member.toUser( ) );
                } catch ( Throwable t ) {
                    log.warn( "Failed to update pc wx info for member[{}], will continue bind without these info.", member.id( ), t );
                }
            } );

            log.info( "Bind pc wx[unionId={},pcWxOpenId={}] to member[{}].", wxUnionId, pcWxOpenId, member.id( ) );
            memberRepository.save( member );
        }
    }
}
