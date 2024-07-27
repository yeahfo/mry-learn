package io.github.yeahfo.mry.learn.common.wx.auth.mobile;

import lombok.Builder;

@Builder
public record MobileWxAuthUserInfo( String openId,
                                    String nickname,
                                    String headerImageUrl ) {
}
