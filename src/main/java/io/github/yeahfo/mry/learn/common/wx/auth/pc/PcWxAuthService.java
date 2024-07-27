package io.github.yeahfo.mry.learn.common.wx.auth.pc;

import java.util.Optional;

public interface PcWxAuthService {
    PcWxAuthAccessTokenInfo fetchAccessToken( String code );

    PcWxAuthUserInfo fetchUserInfo( String accessToken, String pcWxOpenId );

    Optional< String > getAccessToken( String unionId );
}
