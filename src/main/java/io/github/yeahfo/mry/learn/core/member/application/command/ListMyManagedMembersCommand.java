package io.github.yeahfo.mry.learn.core.member.application.command;

import io.github.yeahfo.mry.learn.core.common.validation.id.department.DepartmentId;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.MAX_GENERIC_NAME_LENGTH;

@Builder
public record ListMyManagedMembersCommand( @DepartmentId
                                           String departmentId,

                                           @Size( max = 50 )
                                           String search,

                                           @Size( max = MAX_GENERIC_NAME_LENGTH )
                                           String sortedBy,

                                           boolean ascSort,

                                           @Min( 1 )
                                           int page,

                                           @Min( 10 )
                                           @Max( 100 )
                                           int size ) {
}
