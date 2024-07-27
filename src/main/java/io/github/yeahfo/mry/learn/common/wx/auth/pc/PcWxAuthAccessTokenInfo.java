package io.github.yeahfo.mry.learn.common.wx.auth.pc;

import lombok.Builder;

@Builder
public record PcWxAuthAccessTokenInfo( String accessToken,
                                       String openId,
                                       String unionId ) {
}
