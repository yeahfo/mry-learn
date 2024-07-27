package io.github.yeahfo.mry.learn.core.login.domain;

import lombok.Builder;

@Builder
public record WxIdInfo( String pcWxOpenId,
                        String mobileWxOpenId,
                        String wxUnionId ) {
}
