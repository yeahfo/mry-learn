package io.github.yeahfo.mry.learn.core.login.resource;

import io.github.yeahfo.mry.learn.common.ratelimit.RateLimiter;
import io.github.yeahfo.mry.learn.common.security.jwt.JwtCookieFactory;
import io.github.yeahfo.mry.learn.common.wx.auth.mobile.MobileWxAuthAccessTokenInfo;
import io.github.yeahfo.mry.learn.common.wx.auth.mobile.MobileWxAuthService;
import io.github.yeahfo.mry.learn.common.wx.auth.mobile.MobileWxAuthUserInfo;
import io.github.yeahfo.mry.learn.core.login.application.LoginApplicationService;
import io.github.yeahfo.mry.learn.core.login.domain.WxJwtService;
import io.github.yeahfo.mry.learn.core.member.domain.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.apache.commons.lang3.StringUtils.isNotBlank;


@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
public class MobileWxOAuth2LoginResource {
    private final RateLimiter rateLimiter;
    private final WxJwtService wxJwtService;
    private final JwtCookieFactory jwtCookieFactory;
    private final MemberRepository memberRepository;
    private final MobileWxAuthService mobileWxAuthService;
    private final LoginApplicationService loginApplicationService;
    @Value( "${spring.web.resources.mobile-domain-url:http://localhost:8080/login}" )
    private String domainUri;
    @Value( "${spring.web.resources.mobile-default-url:http://localhost:8080/operations/my-apps}" )
    private String defaultUrl;

    @GetMapping( "/mobile-wx/auth2-callback" )
    public String callback( @RequestParam( "code" ) @Size( max = 100 ) String code,
                            @CookieValue( value = "fromUrl", defaultValue = "" ) String fromUrl,
                            HttpServletResponse response ) {
        rateLimiter.applyFor( "Wx:MobileCallback", 500 );
        MobileWxAuthAccessTokenInfo accessToken = mobileWxAuthService.fetchAccessToken( code );
        String mobileWxOpenId = accessToken.openId( );
        String wxUnionId = accessToken.unionId( );
        return memberRepository.findByWxUnionId( wxUnionId )
                .map( member -> {
                    MobileWxAuthUserInfo userinfo = mobileWxAuthService.fetchUserInfo( accessToken.accessToken( ), mobileWxOpenId );
                    if ( member.updateMobileWxInfo( mobileWxOpenId, userinfo.nickname( ), userinfo.headerImageUrl( ), member.toUser( ) ) ) {
                        log.info( "Updated user mobile wx info:[{}]-[{}]", mobileWxOpenId, userinfo.nickname( ) );
                        memberRepository.save( member );
                    }
                    //登录成功，植入cookie
                    String jwt = loginApplicationService.wxLoginMember( member.id( ) );
                    response.addCookie( jwtCookieFactory.newJwtCookie( jwt ) );
                    //如果没有先前页面，则重定向到默认主页
                    String redirectUrl = isNotBlank( fromUrl ) ? fromUrl : defaultUrl;
                    log.info( "Member[{}] logged in via mobile wx[unionId={}].", member.id( ), wxUnionId );
                    return "redirect:" + redirectUrl;
                } )
                .orElseGet( ( ) -> {
                    //未绑定时，返回登录界面，并同时带上openId和unionId(包含在jwt中)以便后续成功登陆后自动绑定
                    log.info( "Mobile wx openId[{}] with unionId[{}] not bound to a member, redirect to login page.", mobileWxOpenId, wxUnionId );
                    return String.format( "redirect:%s?wx=%s&from=%s", domainUri, wxJwtService.generateMobileWxIdInfoJwt( wxUnionId, mobileWxOpenId ), fromUrl );
                } );
    }
}
