package io.github.yeahfo.mry.learn.core.member.application.command;

import io.github.yeahfo.mry.learn.core.common.application.Command;
import io.github.yeahfo.mry.learn.core.common.domain.UploadedFile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateMyAvatarCommand( @Valid
                                     @NotNull UploadedFile avatar ) implements Command {
}
