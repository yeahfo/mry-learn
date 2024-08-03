package io.github.yeahfo.mry.learn.core.member.application.representation;

import io.github.yeahfo.mry.learn.core.common.domain.Role;
import io.github.yeahfo.mry.learn.core.common.domain.UploadedFile;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record ListMemberRepresentation( String id,
                                        String name,
                                        List< String > departmentIds,
                                        UploadedFile avatar,
                                        boolean active,
                                        Role role,
                                        String mobile,
                                        String wxUnionId,
                                        String wxNickName,
                                        String email,
                                        Instant createdAt ) {
}
