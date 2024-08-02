package io.github.yeahfo.mry.learn.core.login.resource;

import io.github.yeahfo.mry.learn.common.ratelimit.RateLimiter;
import io.github.yeahfo.mry.learn.common.security.jwt.JwtCookieFactory;
import io.github.yeahfo.mry.learn.common.wx.auth.pc.PcWxAuthAccessTokenInfo;
import io.github.yeahfo.mry.learn.common.wx.auth.pc.PcWxAuthService;
import io.github.yeahfo.mry.learn.common.wx.auth.pc.PcWxAuthUserInfo;
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
public class PcWxOAuth2LoginResource {
    private final RateLimiter rateLimiter;
    private final WxJwtService wxJwtService;
    private final PcWxAuthService pcWxAuthService;
    private final JwtCookieFactory jwtCookieFactory;
    private final MemberRepository memberRepository;
    private final LoginApplicationService loginApplicationService;
    @Value( "${spring.web.resources.pc-domain-url:http://localhost:8080/login}" )
    private String domainUri;
    @Value( "${spring.web.resources.pc-default-url:http://localhost:8080/management/my-apps}" )
    private String defaultUrl;

    @GetMapping( "/pc-wx/auth2-callback" )
    public String callback( @RequestParam( "code" ) @Size( max = 100 ) String code,
                            @CookieValue( value = "fromUrl", defaultValue = "" ) String fromUrl,
                            HttpServletResponse response ) {
        rateLimiter.applyFor( "Wx:PcCallback", 500 );

        PcWxAuthAccessTokenInfo accessToken = pcWxAuthService.fetchAccessToken( code );
        String pcWxOpenId = accessToken.openId( );
        String wxUnionId = accessToken.unionId( );
        return memberRepository.findByWxUnionId( wxUnionId )
                .map( member -> {
                    // 每次微信登录时均获取最新的昵称和头像并保存
                    PcWxAuthUserInfo userinfo = pcWxAuthService.fetchUserInfo( accessToken.accessToken( ), pcWxOpenId );
                    if ( member.updatePcWxInfo( pcWxOpenId, userinfo.nickname( ), userinfo.headerImageUrl( ), member.toUser( ) ) ) {
                        log.info( "Updated user pc wx info:[{}]-[{}]", pcWxOpenId, userinfo.nickname( ) );
                        memberRepository.save( member );
                    }
                    //登录成功，植入cookie
                    String jwt = loginApplicationService.wxLoginMember( member.identifier( ) );
                    response.addCookie( jwtCookieFactory.newJwtCookie( jwt ) );
                    //如果没有先前页面，则重定向到默认主页
                    String redirectUrl = isNotBlank( fromUrl ) ? fromUrl : defaultUrl;
                    log.info( "Updated user pc wx info:[{}]-[{}]", pcWxOpenId, userinfo.nickname( ) );
                    return "redirect:" + redirectUrl;
                } )
                .orElseGet( ( ) -> {
                    //未绑定时，返回登录界面，并同时带上openId和unionId(包含在jwt中)以便后续成功登陆后自动绑定
                    log.info( "PC wx openId[{}] with unionId[{}] not bound to a member, redirect to login page.", pcWxOpenId, wxUnionId );
                    return String.format( "redirect:%s?wx=%s&from=%s", domainUri, wxJwtService.generateMobileWxIdInfoJwt( wxUnionId, pcWxOpenId ), fromUrl );
                } );
    }
}
