package io.github.yeahfo.mry.learn.core.member.application.representation;

import io.github.yeahfo.mry.learn.core.common.domain.Role;
import io.github.yeahfo.mry.learn.core.common.domain.UploadedFile;
import io.github.yeahfo.mry.learn.core.tenant.application.representation.ConsoleTenantProfileRepresentation;
import lombok.Builder;

import java.util.List;

@Builder
public record ConsoleMemberProfileRepresentation( String memberId,
                                                  String tenantId,
                                                  String name,
                                                  Role role,
                                                  UploadedFile avatar,
                                                  boolean hasManagedApps,
                                                  ConsoleTenantProfileRepresentation tenantProfile,
                                                  List< String > topAppIds,
                                                  boolean mobileIdentified ) {
}
