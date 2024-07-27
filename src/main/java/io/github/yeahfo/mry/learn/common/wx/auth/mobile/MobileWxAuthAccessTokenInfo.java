package io.github.yeahfo.mry.learn.common.wx.auth.mobile;

import lombok.Builder;

@Builder
public record MobileWxAuthAccessTokenInfo( String accessToken,
                                           String openId,
                                           String unionId ) {
}
