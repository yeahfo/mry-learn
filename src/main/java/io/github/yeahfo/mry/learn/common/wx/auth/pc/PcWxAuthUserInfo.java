package io.github.yeahfo.mry.learn.common.wx.auth.pc;

import lombok.Builder;

@Builder
public record PcWxAuthUserInfo( String openId,
                                String nickname,
                                String headerImageUrl ) {
}
