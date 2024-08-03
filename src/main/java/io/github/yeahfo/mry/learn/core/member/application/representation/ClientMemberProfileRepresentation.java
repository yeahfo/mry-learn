package io.github.yeahfo.mry.learn.core.member.application.representation;

import io.github.yeahfo.mry.learn.core.common.domain.UploadedFile;
import lombok.Builder;

import java.util.List;

@Builder
public record ClientMemberProfileRepresentation( String memberId,
                                                 String memberName,
                                                 UploadedFile avatar,
                                                 String tenantId,
                                                 String tenantName,
                                                 UploadedFile tenantLogo,
                                                 String subdomainPrefix,
                                                 boolean subdomainReady,
                                                 List< String > topAppIds,
                                                 boolean hideBottomMryLogo,
                                                 boolean reportingAllowed,
                                                 boolean kanbanAllowed,
                                                 boolean assignmentAllowed ) {
}
