package io.github.yeahfo.mry.learn.common.wx.auth.mobile;

import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyExtractionOptions;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.SYSTEM_ERROR;
import static io.restassured.RestAssured.given;
import static java.time.Duration.ofHours;

@Component
@RequiredArgsConstructor
public class DefaultMobileWxAuthService implements MobileWxAuthService {
    private static final String MOBILE_WX_AUTH_ACCESS_TOKEN_PREFIX = "MobileWxAuthAccessToken:";
    @Value( "${spring.security.oauth2.client.registration.wechat-mobile.client-id}" )
    private String clientId;
    @Value( "${spring.security.oauth2.client.registration.wechat-mobile.client-secret}" )
    private String clientSecret;
    @Value( "${spring.security.oauth2.client.provider.wechat-mobile.token-uri}" )
    private String tokenUri;
    @Value( "${spring.security.oauth2.client.provider.wechat-mobile.user-info-uri}" )
    private String userinfoUri;
    @Value( "${spring.security.oauth2.client.provider.wechat-mobile.user-info-authentication-method}" )
    private String userinfoAuthenticationMethod;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public MobileWxAuthAccessTokenInfo fetchAccessToken( String code ) {
        String url = String.format( "%s?appid=%s&secret=%s&code=%s&grant_type=%s",
                tokenUri, clientId, clientSecret, code, userinfoAuthenticationMethod );
        ExtractableResponse< Response > response = given( )
                .baseUri( url )
                .when( )
                .get( )
                .then( )
                .extract( );
        return Optional.of( response )
                .filter( when -> Objects.equals( when.statusCode( ), 200 ) )
                .map( ResponseBodyExtractionOptions::jsonPath )
                .filter( when -> StringUtils.isNotBlank( when.getString( "openid" ) ) )
                .filter( when -> StringUtils.isNotBlank( when.getString( "unionid" ) ) )
                .filter( when -> StringUtils.isNotBlank( when.getString( "access_token" ) ) )
                .map( then -> MobileWxAuthAccessTokenInfo.builder( )
                        .accessToken( then.getString( "access_token" ) )
                        .openId( then.getString( "openid" ) )
                        .unionId( then.getString( "unionid" ) )
                        .build( ) )
                .stream( ).peek( userinfo -> stringRedisTemplate.opsForValue( )
                        .set( MOBILE_WX_AUTH_ACCESS_TOKEN_PREFIX + userinfo.unionId( ), userinfo.accessToken( ), ofHours( 1 ) ) )
                .findAny( )
                .orElseThrow( ( ) -> new MryException( SYSTEM_ERROR, "Failed to get mobile wx web auth access token.",
                        Map.of( "response", response.jsonPath( ).get( ) ) ) );
    }

    @Override
    public MobileWxAuthUserInfo fetchUserInfo( String accessToken, String mobileWxOpenId ) {
        String uri = String.format( "%s?access_token=%s&openid=%s&lang=zh_CN", userinfoUri, accessToken, mobileWxOpenId );
        ExtractableResponse< Response > response = given( )
                .baseUri( uri )
                .when( )
                .get( )
                .then( )
                .extract( );
        return Optional.of( response )
                .filter( when -> Objects.equals( when.statusCode( ), 200 ) )
                .map( ResponseBodyExtractionOptions::jsonPath )
                .filter( when -> StringUtils.isNotBlank( "nickname" ) )
                .filter( when -> StringUtils.isNotBlank( "headimgurl" ) )
                .map( then -> MobileWxAuthUserInfo.builder( )
                        .nickname( then.getString( "nickname" ) )
                        .headerImageUrl( then.getString( "headimgurl" ) )
                        .openId( mobileWxOpenId )
                        .build( ) )
                .orElseThrow( ( ) -> new MryException( SYSTEM_ERROR, "Failed to get mobile wx user info.",
                        Map.of( "response", response.jsonPath( ).get( ) ) ) );
    }

    @Override
    public Optional< String > getAccessToken( String unionId ) {
        return Optional.ofNullable( stringRedisTemplate.opsForValue( ).get( MOBILE_WX_AUTH_ACCESS_TOKEN_PREFIX + unionId ) );
    }
}
