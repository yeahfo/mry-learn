package io.github.yeahfo.mry.learn.core.member.application.command;

import io.github.yeahfo.mry.learn.core.common.application.Command;
import io.github.yeahfo.mry.learn.core.common.domain.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import static io.github.yeahfo.mry.learn.core.common.domain.Role.ROBOT;
import static io.github.yeahfo.mry.learn.core.common.exception.MryException.requestValidationException;

@Builder
public record UpdateMemberRoleCommand( @NotNull Role role ) implements Command {
    @Override
    public void correctAndValidate( ) {
        if ( role == ROBOT ) {
            throw requestValidationException( "Role value not allowed." );
        }
    }
}
