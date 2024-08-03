package io.github.yeahfo.mry.learn.core.member.application.representation;

import io.github.yeahfo.mry.learn.core.common.domain.Role;
import lombok.Builder;

import java.util.List;

@Builder
public record MemberInfoRepresentation( String memberId,
                                        String tenantId,
                                        String name,
                                        String email,
                                        String mobile,
                                        Role role,
                                        String wxNickName,
                                        boolean wxBound,
                                        List< String > departments ) {
}
