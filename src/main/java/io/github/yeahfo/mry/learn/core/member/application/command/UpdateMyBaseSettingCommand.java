package io.github.yeahfo.mry.learn.core.member.application.command;

import io.github.yeahfo.mry.learn.core.common.application.Command;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.MAX_GENERIC_NAME_LENGTH;

@Builder
public record UpdateMyBaseSettingCommand( @NotBlank
                                          @Size( max = MAX_GENERIC_NAME_LENGTH )
                                          String name ) implements Command {
}
