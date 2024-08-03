package io.github.yeahfo.mry.learn.core.member.domain;

import lombok.Builder;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Builder
public record MemberReference( String id,
                               String name,
                               String mobile,
                               String email ) {
    public String memberWithEmailText() {
        if (isBlank(email)) {
            return name;
        }
        return name + "(" + email + ")";
    }

    public String memberWithMobileText() {
        if (isBlank(mobile)) {
            return name;
        }
        return name + "(" + mobile + ")";
    }
}
